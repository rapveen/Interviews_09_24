// Types for session management
interface Session {
  sessionId: string;
  userId: string;
  createdAt: Date;
  lastActivity: Date;
  expiresAt: Date;
  refreshToken?: string;
  deviceInfo: {
    userAgent: string;
    ip: string;
  };
}

interface SessionConfig {
  shortTermTTL: number;    // 30 minutes in milliseconds
  mediumTermTTL: number;   // 24 hours in milliseconds
  longTermTTL: number;     // 30 days in milliseconds
  refreshTokenTTL: number; // 60 days in milliseconds
}

// Session management service
class SessionManager {
  private readonly config: SessionConfig = {
    shortTermTTL: 30 * 60 * 1000,
    mediumTermTTL: 24 * 60 * 60 * 1000,
    longTermTTL: 30 * 24 * 60 * 60 * 1000,
    refreshTokenTTL: 60 * 24 * 60 * 60 * 1000,
  };

  constructor(
    private readonly redis: Redis,
    private readonly jwt: JWTService,
    private readonly config: SessionConfig
  ) {}

  async createSession(userId: string, deviceInfo: any): Promise<Session> {
    const sessionId = crypto.randomUUID();
    const refreshToken = this.generateRefreshToken();
    
    const session: Session = {
      sessionId,
      userId,
      createdAt: new Date(),
      lastActivity: new Date(),
      expiresAt: new Date(Date.now() + this.config.shortTermTTL),
      refreshToken,
      deviceInfo
    };

    // Store session in Redis with appropriate TTL
    await this.redis.setex(
      `session:${sessionId}`,
      Math.floor(this.config.shortTermTTL / 1000),
      JSON.stringify(session)
    );

    // Store refresh token with longer TTL
    await this.redis.setex(
      `refresh:${refreshToken}`,
      Math.floor(this.config.refreshTokenTTL / 1000),
      userId
    );

    return session;
  }

  async validateSession(sessionId: string): Promise<Session | null> {
    const sessionData = await this.redis.get(`session:${sessionId}`);
    if (!sessionData) return null;

    const session: Session = JSON.parse(sessionData);
    
    // Check if session is expired
    if (new Date() > new Date(session.expiresAt)) {
      await this.invalidateSession(sessionId);
      return null;
    }

    return session;
  }

  async refreshSession(sessionId: string): Promise<Session | null> {
    const session = await this.validateSession(sessionId);
    if (!session) return null;

    // Calculate new expiration based on last activity
    const timeSinceLastActivity = Date.now() - new Date(session.lastActivity).getTime();
    let newTTL = this.config.shortTermTTL;

    if (timeSinceLastActivity <= this.config.shortTermTTL) {
      newTTL = this.config.shortTermTTL;
    } else if (timeSinceLastActivity <= this.config.mediumTermTTL) {
      newTTL = this.config.mediumTermTTL;
    } else if (timeSinceLastActivity <= this.config.longTermTTL) {
      newTTL = this.config.longTermTTL;
    } else {
      await this.invalidateSession(sessionId);
      return null;
    }

    // Update session
    session.lastActivity = new Date();
    session.expiresAt = new Date(Date.now() + newTTL);

    await this.redis.setex(
      `session:${sessionId}`,
      Math.floor(newTTL / 1000),
      JSON.stringify(session)
    );

    return session;
  }

  async invalidateSession(sessionId: string): Promise<void> {
    await this.redis.del(`session:${sessionId}`);
  }

  private generateRefreshToken(): string {
    return crypto.randomBytes(32).toString('hex');
  }
}

// Express middleware implementation
const authMiddleware = (sessionManager: SessionManager) => {
  return async (req: Request, res: Response, next: NextFunction) => {
    const sessionId = req.cookies['sessionId'];
    
    if (!sessionId) {
      return res.status(401).json({ error: 'No session found' });
    }

    try {
      const session = await sessionManager.validateSession(sessionId);
      
      if (!session) {
        res.clearCookie('sessionId');
        return res.status(401).json({ error: 'Invalid session' });
      }

      // Refresh session if needed
      const refreshedSession = await sessionManager.refreshSession(sessionId);
      if (refreshedSession) {
        res.cookie('sessionId', sessionId, {
          httpOnly: true,
          secure: process.env.NODE_ENV === 'production',
          sameSite: 'strict',
          expires: refreshedSession.expiresAt
        });
      }

      // Attach session to request for later use
      req.session = session;
      next();
    } catch (error) {
      next(error);
    }
  };
};

// Login endpoint implementation
const loginHandler = async (req: Request, res: Response) => {
  const { userId, password } = req.body;

  try {
    // Validate credentials (implementation details omitted)
    const isValid = await validateCredentials(userId, password);
    if (!isValid) {
      return res.status(401).json({ error: 'Invalid credentials' });
    }

    // Create new session
    const deviceInfo = {
      userAgent: req.headers['user-agent'],
      ip: req.ip
    };

    const session = await sessionManager.createSession(userId, deviceInfo);

    // Set session cookie
    res.cookie('sessionId', session.sessionId, {
      httpOnly: true,
      secure: process.env.NODE_ENV === 'production',
      sameSite: 'strict',
      expires: session.expiresAt
    });

    // Return user data
    const userData = await getUserData(userId);
    res.json({ user: userData });
  } catch (error) {
    res.status(500).json({ error: 'Internal server error' });
  }
};