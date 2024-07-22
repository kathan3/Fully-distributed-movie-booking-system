import requests

userServiceURL = "http://localhost:8080"
bookingServiceURL = "http://localhost:8081"
walletServiceURL = "http://localhost:8082"

def main():
    name = "John Doe"
    email = "johndoe@mail.com"
    name2 = "Kathan"
    email2 = "Kathan@gmail.com"
    showID = 1
    delete_users()
    User1 = create_user(name,email)
    User2 = create_user(name2,email2)
    userId1 = User1.json().get("id")
    userId2 = User2.json().get("id")
    wallet1 = update_wallet(userId1,"credit",7777)
    wallet2 = update_wallet(userId2,"credit",7777)
    responseDoBooking = doBooking(showID,userId1,40)
    responseDeletedBooking =  DeleteBookingByUserId(userId1)
    responseDoBookingOfUSer2 = doBooking(showID,userId2,15)
    if(responseDoBookingOfUSer2.status_code == 200):
        print("Test Passed It means that When we deleted booking the seats will be added back in booking")
    else :
        print("Failed")

def create_user(name, email):
    new_user = {"name": name, "email": email}
    response = requests.post(userServiceURL +"/users", json=new_user)
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

def DeleteBookingByUserId(user_id):
    response = requests.delete(bookingServiceURL+f"/bookings/users/{user_id}")
    return response



if __name__ == "__main__":
    main()