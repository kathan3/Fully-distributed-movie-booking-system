import requests

userServiceURL = "http://localhost:8080"
bookingServiceURL = "http://localhost:8081"
walletServiceURL = "http://localhost:8082"

def main():
    name1 = "John Doe"
    email1 = "johndoe@mail.com"
    name2 = "Jane Doe"
    email2 = "janedoes@mail.com"
    show1_id = 1
    show2_id = 2
    test(name1, name2, email1, email2, show1_id, show2_id)

def create_user(name, email):
    new_user = {"name": name, "email": email}
    response = requests.post(userServiceURL + "/users", json=new_user)
    return response

def get_wallet(user_id):
    response = requests.get(walletServiceURL + f"/wallets/{user_id}")
    return response

def update_wallet(user_id, action, amount):
    response = requests.put(walletServiceURL + f"/wallets/{user_id}", json={"action":action, "amount":amount})
    return response

def get_user_details(user_id):
    response = requests.get(bookingServiceURL + f"/bookings/users/{user_id}")
    return response

def delete_users():
    requests.delete(userServiceURL+f"/users")    

def delete_wallets():
    requests.delete(walletServiceURL+f"/wallets")    

def test(name1, name2, email1, email2, balance1, balance2):
    try:
        delete_users()
        # Create two users
        create_response1 = create_user(name1, email1)
        user_id1 = create_response1.json()['id']
        create_response2 = create_user(name2, email2)
        user_id2 = create_response2.json()['id']
        
        # Credit balance to both users
        update_wallet(user_id1, "credit", balance1)
        update_wallet(user_id2, "credit", balance2)

        # Delete wallets of all users
        delete_wallets() 

        # They must not have any wallets
        if(get_wallet(user_id1).status_code != 404 or get_wallet(user_id2).status_code != 404):
            print("Test failed") 
        else:
            print("Test passed") 

    except:
        print("Some Exception Occurred")

if __name__ == "__main__":
    main()
