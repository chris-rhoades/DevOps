# -*- coding: utf-8 -*-
"""
Created on Tue Apr  6 18:40:03 2021

@author: zvasco.chironda
"""

# function which return reverse of a string

def isPalindrome(inputs):
    if(inputs==inputs[::-1]):
      print("The string is a palindrome")
      return inputs
    else:
      print("The input is Not a palindrome")
    
      
s=input(("Enter a string:"))    
isPalindrome(s)
