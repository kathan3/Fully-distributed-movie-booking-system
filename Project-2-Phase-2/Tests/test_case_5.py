import requests

userServiceURL = "http://localhost:8080"
bookingServiceURL = "http://localhost:8081"
walletServiceURL = "http://localhost:8082"

def main():
    name = "John Doe"
    email = "johndoe@mail.com"
    balance = 1000
    test(name, email, balance)

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

def delete_users():
    requests.delete(userServiceURL+f"/users")    

def test(name, email, balance):
    try:
        delete_users()
        # Create a user
        create_response = create_user(name, email)
        user_id = create_response.json()['id']
        
        # Attempt to pass text other than "debit" or "credit" to PUT /wallets/{user_id} endpoint
        update_response1 = update_wallet(user_id, "dummy", balance)
        if(update_response1.status_code != 404):
            print("Test failed")
            return
        
        # Credit balance to user
        update_response2 = update_wallet(user_id, "credit", balance)
        if(update_response2.status_code != 200):
            print("Test failed")
            return

        # Make sure that the amount has been credited
        get_wallet_response = get_wallet(user_id)
        if(get_wallet_response.status_code != 200 or not (get_wallet_response.json()['balance'] == update_response2.json()['balance'] == balance)):
            print("Test failed")
            return

        # Attempt to debit more money than amount in wallet
        update_response3 = update_wallet(user_id, "debit", balance * 2)
        if(update_response3.status_code != 400):
            print("Test failed")
        else:
            print("Test passed")

    except:
        print("Some Exception Occurred")

if __name__ == "__main__":
    main()
