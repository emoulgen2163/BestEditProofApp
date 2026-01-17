# Feature List for Best Edit & Proof Android App

This document outlines the feature set divided into **Public**, **Authenticated (Client)**, and **Admin/Editor** groups. It will be used for scope definition and sprint planning.

---

## **1. Public Features (Login Required)**

### Registration & Login

* This app is only for direct ordering. The wide details have already been on the website, therefore not considered needed in the app
* Login with your account if you already have one
* Create account with email/password
* Validate email format and password strength
* Forgot password flow
* Login form with error handling

---

## **2. Authenticated User Features (Client Portal)**

### **2.1 Dashboard**

* Overview of active, pending, and completed orders
* Notifications panel

### **2.2 Place an Order**

* Upload document(s)
* Select service type (editing/proofreading)
* Select subject area
* Input instructions for editor
* Select turnaround time
* Final price calculation
* Apply coupons (if available)
* Submit order

### **2.3 Payment**

* Payment via Stripe/PayPal (depending on backend availability)
* View payment confirmation
* Download invoices

### **2.4 Order Tracking & Management**

* View order details
* Order timeline (submitted → assigned → in progress → completed)
* Download completed edited document
* View revision requests
* Request revisions (if allowed)

### **2.6 Profile & Settings**

* Edit account details
* Change password
* Manage notification preferences
* Delete account

### **2.7 Document History**

* View all past orders
* Download past edited files
* View receipts & payments
