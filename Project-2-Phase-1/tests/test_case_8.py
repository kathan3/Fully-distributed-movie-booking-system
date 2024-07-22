import requests
import sys
from utils import *
from user import *
from wallet import *
from booking import *

# Booking a non-existant Show
# Creates a user, creates wallet, deletes it's wallets, creates a booking

def main():
    response = delete_users()
    if not check_response_status_code(response, 200):
        return False
    
    response = delete_wallets()
    if not check_response_status_code(response, 200):
        return False

    name = "Anurag Kumar"
    email = "ak47@iisc.ac.in"
    show_id = 99
    seats_booked = 10

    user_response = post_user(name, email)
    if not test_post_user(name, email, user_response):
        return False
    user = user_response.json()
    user_id = user['id']

    wallet_response = put_wallet(user_id, "credit", 1000)
    if not test_put_wallet(user_id, "credit", 1000, 0, wallet_response):
        return False
    wallet = wallet_response.json()

    booking_response = post_booking(user_id, show_id, seats_booked)
    if not check_response_status_code(booking_response, 400):
        return False

    return True

if __name__ == "__main__":
    if main():
        sys.exit(0)
    else:
        sys.exit(1)
