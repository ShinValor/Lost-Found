
# Lost-Found

## Table of Contents

1. [Introduction](#Introduction)
2. [Usage](#Usage)
3. [Technologies](#Technologies)
4. [Wireframe](#Wireframe)
5. [Unified Modeling Language](#Unified-Modeling-Language)
6. [Contributor](#Contributor)

## Introduction

Lost & Found is an android application that allows CUNY students to post what they lost or found. The application has two main screen, Lost and Found tab. Lost tab is for user who lost their items on campus, whether it’s laptop or water bottle. The Found tab is for user who found items through the campus, for example school ID. Users can create post by taking a photo and writing a description of the lost/found item. The owner of the item can contact the user who found the item through Message, Call or Track features. Users can message each other by using the built-in messaging system. Users can also call if preferred. To return the item to the owner, user can use the Track feature. The Track is a feature that send an email to the user who found the item so that he/she can contact the owner of the item. User must fill out a form to verify the user identify (e.g. full name, school id, phone number). Then, the user need to sign the form through the built-in signature pad to accept the terms and conditions.

## Usage
1) Open up the app and create an account.
2) Verify the account using the email you registered with.
3) Login and set up your profile and save your information.

## Technologies
- Material UI
- Android
- Firebase

## Wireframe    

![Wireframe](./imgs/wireframe.png)

## Unified Modeling Language  

![UML](./imgs/uml.png)

**Activities**  
- LoginActivity - Activity for user to enter their email and password.   
- RegisterActivity - Activity for user to register for an account using their email and password.   
- MainActivity - Home activity for user to view between the Lost & Found tab to see other users' post. 
- MessageActivity - Activity to allow user to message another user.
- MessageViewActivity - Activity to view chats the user has created.
- PostActivity - Activity to allow user to post for a lost or found item.
- PostViewActivity - Activity to view other user’s post, either lost or found.
- ProfileActivity - Activity to view the user's profile and edit user's information.
- ProfileViewActivity - Activity to view other user's profile

**Fragments**  
- LostFragment - Fragment of the Lost tab.  
- FoundFragment - Fragment of the Found tab.  

**Classes**  
- Message - Class to hold a message data e.g. user and message.  
- MessageList - Class to hold all message data of that user.  
- Post - Class to hold a post information (e.g. title and description).  
- PostList - Class to hold all post data of either Lost or Found tab.  
- SecurityQuestions - Class to hold the security questions when a user want to track and contact other user.  
- Upload - Class to hold the post image url.   
- User - Class to hold a user information e.g. name, phonenumber, id,and school  
- GMailSender - Class to send email to a user gmail account when a user is using track feature.  
- JSSEProvider - Class to send email to a user gmail account when a user is using track feature.

**Adapters**  
- MessageAdapter - Extends ArrayAdapter to populates a list of messages into a messageview.
- MessageViewAdapter - Extends ArrayAdapter to populates a list of chats into a chatting view.
- PostAdapter - Extends ArrayAdapter to populates a list of posts into a Lost or Found fragment view.  
- ViewPagerAdapter - Extends FragmentPagerAdapter to add fragments,lost & found, to switch between lost & found tab.

## Contributor  
Ho Ming Poon
