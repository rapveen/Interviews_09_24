1. Design TinyURL service
- each long_url should have only one short_url
- redirection the url
- no validation requires for the link
- expected a scale of 10M/day encoding shortURls
1. how many chars is needed
2. what is the data size we are expecting
3. how do you handle these 3 scenarios?
    - concurrent users trying to do shorteing of url for the lonng url
    - how it handles collisions in shortUrl
    - how it cheks if a longUrl related shortURL is already present in KV database
    - when checking for uniqueness and adding into 2 DBs, what happens if there is a failure in between.
    - 
4. They specifically asked code for it.
5. I said we can use KV db store for mapping (longUrl : shortUrl) this will help us to retrieve the info faster.
cons:
1. I coudldnt complete the design
2. I didnt start the diagram itself.
3. 
