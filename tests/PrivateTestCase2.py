import requests

userServiceURL = "http://localhost:8080"
bookingServiceURL = "http://localhost:8081"
walletServiceURL = "http://localhost:8082"

def main():
    name = "John Doe"
    email = "johndoe@mail.com"
    showID = 7
    delete_users()
    response = create_user(name,email)
    print(response.json())
    user_id = response.json().get("id")
    wallet = update_wallet(user_id,"credit",777)
    r = doBooking(showID,user_id,3)
    print(r.status_code)
    deleteUsersById(user_id)
    CheckForBookingAndWallet(user_id)

def create_user(name, email):
    new_user = {"name": name, "email": email}
    response = requests.post(userServiceURL + "/users", json=new_user)
    return response

def get_wallet(user_id):
    response = requests.get(walletServiceURL + f"/wallets/{user_id}")
    return response

def doBooking(show_id,user_id,seatsBooked):
    RequestDTO = {"show_id":show_id, "user_id": user_id, "seats_booked": seatsBooked}
    response = requests.post(bookingServiceURL+f"/bookings",json=RequestDTO)
    return response

def update_wallet(user_id, action, amount):
    response = requests.put(walletServiceURL + f"/wallets/{user_id}", json={"action":action, "amount":amount})
    return response


def deleteUsersById(user_id):
    response = requests.delete(userServiceURL+f"/users/{user_id}")


def delete_users():
    requests.delete(userServiceURL+f"/users")    

def CheckForBookingAndWallet(user_id):
    try:
        responseOfBooking=requests.get(bookingServiceURL + f"/bookings/users/{user_id}")
        responseOfWallet= requests.get(walletServiceURL+ f"/wallets/{user_id}")
        if(responseOfWallet.status_code==404 and responseOfBooking.status_code==404):
            print("Test Case Passed")
        else:
            print("Test Case Failed")
    except:
        print("Some Exception Occurred")

if __name__ == "__main__":
    main()