import requests

userServiceURL = "http://localhost:8080"
bookingServiceURL = "http://localhost:8081"
walletServiceURL = "http://localhost:8082"

def main():
    
    name = "John Doe"
    email = "johndoe@mail.com"
    showID = 7
    walletAmount = 1000
    delete_users()
    UserResponse = create_user(name,email)
    BookingResponse = doBooking(showID,UserResponse.json()["id"],3)
    if(BookingResponse.status_code!=400):
        print("Test Case Failed It should return status code 400")
    else:
        if(IsWalletExist(UserResponse.json()["id"])):
            print("Test Case Passed When we request post http request to bookings it will automatically genrate wallet of the user with balance 0")
        else :
            print("Test Case Failed")


def create_user(name, email):
    new_user = {"name": name, "email": email}
    response = requests.post(userServiceURL + "/users", json=new_user)
    return response


def delete_users():
    requests.delete(userServiceURL+f"/users")    

def doBooking(show_id,user_id,seatsBooked):
    RequestDTO = {"show_id":show_id, "user_id": user_id, "seats_booked": seatsBooked}
    response = requests.post(bookingServiceURL+f"/bookings",json=RequestDTO)
    return response

def IsWalletExist(user_id):
    response = requests.get(walletServiceURL + f"/wallets/{user_id}")
    print(response.json())
    if(response.json()["balance"]==0): 
        return True
    return False

if __name__ == "__main__":
    main()
