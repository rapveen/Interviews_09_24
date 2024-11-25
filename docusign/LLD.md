It was related to expense report. A mockup was provided and asked to start with DB diagram - like schema and columns. The elements were simple enough
3 button - Submit report, save report and one other
Report Id at the top
Employee name, Approver, Department and Location
2 sections
1st section for Expense fields
(a) Expense date
(b) Expense type - 4 values drop down
(c) Amount
(d) Purpose
(e) Location


2nd section for properties related to the report it self-
(a) Report period
(b) Some proeprties through check-box
(c) Another select list for report purpose
(d) Another checkbox for Recurring


At the bottom there is a grid with each individual expenses listed with buttons for deletion or edit.


Now that I had some time to think about it I kind of a good idea what it would like but in there I could not even ask about basic questions like how many reports we are talking about, maximum numbr of expenses in a single report, the duration for which to retain those and so on.


After initial design, they threw a wrench around validation rules. Like for a sepcific employee type a specific expense type can't have more than specific amount or some expense types are not even available.


Would be good to know other's thoughts on how can we envision such a sytem? DB schema and API that will be required.

## Questions to ask
 Will the expense type and other filters be available across all employees or are they tied to any other attribute of the employee or other report parts?


## things to consider

Assuming Relational DB design, you can model it with a master-> detail relationship.


Employee Table can contain all the employee details such as id, name and other meta data.
Report Table can contain Report Id, Start Date, End Date, Employee Id, Report Purpose Code, Recurring(Y/N).....
Report Purpose Table - Kind of reference table Code, Description ....
Expense Type is also another example of reference table - Expense Type, Code, Description..
Expense Table - Employee Id, Expense Code, Expense Amount, Purpose(varchar2), Location(varchar2), Report Id


Hope you get the idea.

=> That's what I went with as well. Relational DB with the relationships as you mentioned. I got bogged down into the rabbit hole of making everything generalized so that it is easier to add new fields/attributes and made it too complicated.
    I think we can also maintain a map of report purpose codes and departments or locations to filter them down and same can be applied for expense type. I am still struggling to come up with a good way of implementing validations where one field affects the validation of other. Implementing it in the UI layer or service is simple but then updating any of the rules will be a nightmare. I am still trying to come up with a good way to store/update/represent these rules so that any code change is mostly not required.
