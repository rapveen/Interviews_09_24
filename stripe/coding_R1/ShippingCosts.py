def calculate_shipping_cost(shipping_costs, order):
    """
    Calculate total shipping cost for an order based on shipping cost rules
    
    Args:
        shipping_costs (dict): Shipping cost rules by country and product
        order (dict): Order details including country and items
    
    Returns:
        int: Total shipping cost for the order
    """
    total_cost = 0
    country = order["country"]
    
    # Get country specific shipping costs
    country_costs = shipping_costs.get(country)
    if not country_costs:
        raise ValueError(f"Shipping costs not found for country: {country}")
    
    # Calculate cost for each item in order
    for order_item in order["items"]:
        product = order_item["product"]
        quantity = order_item["quantity"]
        
        # Find product shipping rules
        product_costs = next(
            (item for item in country_costs if item["product"] == product),
            None
        )
        
        if not product_costs:
            raise ValueError(f"Shipping costs not found for product: {product}")
        
        # Calculate cost based on quantity tiers
        item_cost = 0
        remaining_quantity = quantity
        
        for tier in product_costs["costs"]:
            tier_min = tier["minQuantity"]
            tier_max = tier["maxQuantity"]
            tier_cost = tier["cost"]
            
            if remaining_quantity <= 0:
                break
                
            # Calculate quantity for current tier
            if tier_max is None:
                tier_quantity = remaining_quantity
            else:
                tier_quantity = min(remaining_quantity, tier_max - tier_min)
            
            item_cost += tier_quantity * tier_cost
            remaining_quantity -= tier_quantity
        
        total_cost += item_cost
    
    return total_cost

def main():
    # Shipping cost rules
    shipping_costs = {
        "US": [
            {
                "product": "mouse",
                "costs": [
                    {"minQuantity": 0, "maxQuantity": None, "cost": 550}
                ]
            },
            {
                "product": "laptop",
                "costs": [
                    {"minQuantity": 0, "maxQuantity": 2, "cost": 1000},
                    {"minQuantity": 3, "maxQuantity": None, "cost": 900}
                ]
            }
        ],
        "CA": [
            {
                "product": "mouse",
                "costs": [
                    {"minQuantity": 0, "maxQuantity": None, "cost": 750}
                ]
            },
            {
                "product": "laptop",
                "costs": [
                    {"minQuantity": 0, "maxQuantity": 2, "cost": 1100},
                    {"minQuantity": 3, "maxQuantity": None, "cost": 1000}
                ]
            }
        ]
    }

    # Test orders
    order_us = {
        "country": "US",
        "items": [
            {"product": "mouse", "quantity": 20},
            {"product": "laptop", "quantity": 5}
        ]
    }

    # Calculate and print results
    try:
        cost = calculate_shipping_cost(shipping_costs, order_us)
        print(f"Total shipping cost for US order: {cost}")
        
        # Break down calculation
        print("\nBreakdown:")
        print("Mouse (20 units @ 550): 11000")
        print("Laptop (2 units @ 1000 + 3 units @ 900): 4700")
        print("Total: 15700")
        
    except ValueError as e:
        print(f"Error: {e}")

if __name__ == "__main__":
    main()