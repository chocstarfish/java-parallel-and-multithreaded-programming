# Approach to synchronization
* Lock table availability when customer attempts to enter coffee shop and leaves it
* Lock order list when cooks try to grab one and cook
* Lock machine capacity when cook starts to use machine
* Wait for machines to finish and join for an order
* Interrupt cooks and machines once simulation finishes

# Conditions
## Customer.java
### run()
#### Pre-conditions
* The customer has name, order items and an order number
* The coffee shop has a free table
#### Post-conditions
* The customer leaves the coffee shop when the order is complete
* CustomerLeavingCoffeeShop event should be emitted
#### Invariants
* Once there is a free table for the customer, she/he will place an order
* Each customer should have an unique order number
#### Exceptions
* The customer waits if there is no free table in the coffee shop currently

## Cook.java
### run()
#### Pre-conditions
* The cook should have a name
* There are orders placed by customers for the cook to retrieve
#### Post-conditions
* CookEnding event should be emitted
#### Invariants
* Serve the customers with higher priority first
* The customer of the order should be notified once all items of the order are cooked
#### Exceptions
* Terminate when no order items found in given order

### makeFood()
#### Pre-conditions
* Each order has at least one food item
* There is a machine available to cook
#### Post-conditions
* CookCompletedOrder event should be emitted
#### Invariants
* Food must be made by corresponding machine
* Wait until all food from an order has been cooked by corresponding machines
#### Exceptions
* Wait if no machine available currently

## Machine.java
### makeFood()
#### Pre-conditions
* Food passed from cook should be the same food as the machine is supposed to cook
* The machine is not at full capacity
#### Post-conditions
* Notify the cook once cooking finishes
* MachineDoneFood event should be emitted
#### Invariants
* Machine can only cook one kind of food
* Machine can only cook one food at a time
* It takes a period of time to cook a certain food
#### Exceptions
* Notify the cook if the machine is at full capacity
* Terminates making food if interrupted