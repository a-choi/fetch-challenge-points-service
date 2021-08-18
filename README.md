
## Fetch Rewards Coding Exercise - Backend Software Engineering

###### Author - [Arthur Choi](https://github.com/a-choi)

---

# How to Run

#### Prerequisites
TODO

### _*Assumptions/Clarifications*_:
1) From the rules for determining what points to "spend" first:
    >We want no payer's points to go negative
    
    - One payer can owe points to many users
    - "_payer's points_" `==` points one payer owes to one user
    - "_payer's points_" `!=` sum of points one payer owes to all their users
    - Spending points `!=` new transaction 
2) Points can only be whole numbers (`long`/`int`)
