import requests

userServiceURL = "http://localhost:8080"
bookingServiceURL = "http://localhost:8081"
walletServiceURL = "http://localhost:8082"

def main():
    name = "John Doe"
    email = "johndoe@mail.com"
    show1_id = 1
    show2_id = 2
    test(name, email, show1_id, show2_id)

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

def get_show_details(show_id):
    response = requests.get(bookingServiceURL + f"/shows/{show_id}")
    return response

def get_user_details(user_id):
    response = requests.get(bookingServiceURL + f"/bookings/users/{user_id}")
    return response

def delete_all_bookings():
    response = requests.delete(bookingServiceURL+f"/bookings")
    return response

def delete_users():
    requests.delete(userServiceURL+f"/users")    

def test(name, email, show1_id, show2_id):
    try:
        delete_users()
        # Create a user
        create_response = create_user(name, email)
        user_id = create_response.json()['id']
        # Get show details
        show1_details = get_show_details(show1_id)
        show2_details = get_show_details(show2_id)
        # Credit sufficient balance to user
        wallet_details = update_wallet(user_id, "credit", show1_details.json()['price'] * show1_details.json()['seats_available'] + show2_details.json()['price'] * show2_details.json()['seats_available'])

        # Make 2 bookings in show1 and 1 booking in show2
        booking1 = {"show_id": show1_id, "user_id": user_id, "seats_booked": show1_details.json()['seats_available'] / 4}
        requests.post(bookingServiceURL + "/bookings", json=booking1)
        booking2 = {"show_id": show1_id, "user_id": user_id, "seats_booked": show1_details.json()['seats_available'] / 4}
        requests.post(bookingServiceURL + "/bookings", json=booking2)
        booking3 = {"show_id": show2_id, "user_id": user_id, "seats_booked": show2_details.json()['seats_available'] / 4}
        requests.post(bookingServiceURL + "/bookings", json=booking3)

        # Delete all the bookings
        delete_all_bookings()
        user_booking_details = get_user_details(user_id)
        if(user_booking_details.json() != []):
            print("Test failed")
            return
        
        # Make sure the amount is again credited to the user
        if(get_wallet(user_id).json() != wallet_details.json()):
            print("Test failed2")
        else:
            print("Test passed")

    except:
        print("Some Exception Occurred")

if __name__ == "__main__":
    main()
