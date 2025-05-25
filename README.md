# 🍺 Limerick Brewhub – Beer Importer REST API

Limerick Brewhub is a cutting-edge REST API built with Spring Boot for a fictional Irish importer of beers from around the world.  
It manages customers, breweries, beers, and reviews, delivering data in multiple formats and generating detailed brewery reports in PDF.

---

## 📌 Overview

This API offers detailed beer and brewery insights, allowing customers to share their reviews,  
breweries to showcase their beers, and admin users to generate comprehensive PDF reports.  
Built with scalability and flexibility in mind, it supports JSON, XML, YAML, and TSV data formats,  
alongside robust authentication and transactional data integrity.

---

## 📱 Features

- 🍻 **Customer Beer Experience & Brewery Insights:** Retrieve detailed info including beers reviewed, ratings, and comments.  
- 📄 **Multi-format Data Delivery:** Supports JSON, XML, YAML, and TSV via content negotiation.  
- 📝 **Review Management:** Securely submit and update beer reviews with dynamic rating recalculation.  
- 📊 **PDF Report Generation:** Create rich brewery reports with stats, beer details, and geocode data.  
- 🔐 **Role-based Authentication & Authorization:** Keep sensitive operations protected.  
- 🛒 **Beer Wishlist & Notifications:** Save beers to a wishlist and receive notifications when they are back in stock or available to review.

---

## 🛠️ Built With

- ☕ Spring Boot  
- 🔒 Spring Security  
- 🗄️ MySQL  
- 📦 Maven  
- 📜 Swagger (OpenAPI)  

---

## 🗄️ Database

The MySQL schema (`beerdb`) includes Customers, Beers, Breweries, Reviews, and their relations.  
The `beerdb.sql` file initializes the database structure.

---

## 🔐 Security Notice

⚠️ Sensitive credentials (e.g., database passwords) are **NOT** included in this repository.  
Ensure your local `application.properties` or environment variables securely store your secrets.

---

## 👨‍💻 Author

**Jason Price**  
Final Year BSc. (Hons) Internet Systems Development Student  
Ireland  
[LinkedIn](https://www.linkedin.com/in/jasonpricedev/)
