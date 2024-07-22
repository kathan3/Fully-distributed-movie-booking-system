import requests
import random
import sys
from http import HTTPStatus
from threading import Thread

from user import *
from booking import *
from wallet import *

user1_amount = 0
user2_amount = 0
user1_bookings = [0] * 21
user2_bookings = [0] * 21

# Thread 1: User1 makes a 100 bookings
def t1(user_id):
    global user1_amount
    global user1_bookings
    for i in range(10):
        seats = random.randint(1,4)
        show = random.randint(1, 20)
        show_details = get_show(show).json()
        response = requests.post(bookingServiceURL + f"/bookings", json={'show_id': show, 'user_id': user_id, 'seats_booked': seats})
        print(f"{show} {user_id} {seats}")
        print(response.status_code)
        if response.status_code == 200:
            user1_amount += show_details['price'] * seats
            user1_bookings[show] += seats
        
# Thread 2: User2 makes a 100 bookings
def t2(user_id):
    global user2_amount
    global user2_bookings
    for i in range(10):
        seats = random.randint(1,4)
        show = random.randint(1, 20)
        show_details = get_show(show).json()
        response = requests.post(bookingServiceURL + f"/bookings", json={'show_id': show, 'user_id': user_id, 'seats_booked': seats})
        if response.status_code == 200:
            user2_amount += show_details['price'] * seats
            user2_bookings[show] += seats

def main():
    try:
        
        response = delete_users()
        if not check_response_status_code(response, 200):
            return False
    
        response = delete_wallets()
        if not check_response_status_code(response, 200):
            return False

        response = post_user('ABC','abc@iisc.ac.in')
        if not test_post_user('ABC','abc@iisc.ac.in', response):
            return False
        user1 = response.json()

        response = post_user('DEF','def@iisc.ac.in')
        if not test_post_user('DEF','def@iisc.ac.in', response):
            return False
        user2 = response.json()

        initial_balance = 999999999
        response = put_wallet(user1['id'], 'credit', initial_balance)
        if not test_put_wallet(user1['id'], 'credit', initial_balance, 0, response):
            return False
        response = put_wallet(user2['id'], 'credit', initial_balance)
        if not test_put_wallet(user2['id'], 'credit', initial_balance, 0, response):
            return False
        
        old_show_details = [0]*21
        for show in range(1, 21):
            show_details = get_show(show).json()
            old_show_details[show]=show_details['seats_available']
            print(f"{old_show_details[show]}")
            
        
        ### Parallel Execution Begins ###
        thread1 = Thread(target=t1, kwargs = {'user_id': user1['id']})
        thread2 = Thread(target=t2, kwargs = {'user_id': user2['id']})

        thread1.start()
        thread2.start()

        thread1.join()
        thread2.join()
        ### Parallel Execution Ends ###

        #response = get_wallet(user1['id'])
        #if not test_get_wallet(user1['id'], response, True, initial_balance - user1_amount):
        #    return False
        #response = get_wallet(user2['id'])
        #if not test_get_wallet(user2['id'], response, True, initial_balance - user2_amount):
        #    return False

        for show in range(1, 21):
            show_details = get_show(show).json()
            print(f"{old_show_details[show]}+' (' +{user1_bookings[show]}+' '+ {user2_bookings[show]} + ' )'")
            if not show_details['seats_available'] == old_show_details[show] - (user1_bookings[show] + user2_bookings[show]):
                return False

        return True
    except:
        return False

if __name__ == "__main__":
    if main():
        sys.exit(0)
    else:
        sys.exit(1)
