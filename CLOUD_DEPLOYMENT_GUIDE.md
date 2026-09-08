# 🌐 Pets Care Management System - 24/7 Permanent Cloud Deployment Guide
*(ল্যাপটপ বন্ধ থাকলেও সারা বিশ্ব থেকে ২৪/৭ লাইভ থাকার গাইড)*

---

## 💡 কেন আগের Cloudflare Tunnel লিঙ্ক ল্যাপটপ বন্ধ করলে কাজ করত না?
আগের মেথডটি ছিল **Local Tunnel (`cloudflared.exe`)**। এটি সরাসরি আপনার ল্যাপটপের ভেতরে চলা স্প্রিং বুট সার্ভারের সাথে যুক্ত ছিল। তাই যখনই ল্যাপটপ বন্ধ বা স্লিপে চলে যায়, লোকাল সার্ভার বন্ধ হয়ে যায় এবং ওয়েবসাইট সাথে সাথে অফলাইন হয়ে যায়।

---

## 🚀 ল্যাপটপ ছাড়া ২৪/৭ লাইভ রাখার সমাধান: Cloud Hosting (ক্লাউড হোস্টিং)
সার্ভারকে ইন্টারনেটের ক্লাউড ডেটাসেন্টারে (যেমন Railway অথবা Render) হোস্ট করতে হবে। 
- আপনার ল্যাপটপ বন্ধ থাকলেও ক্লাউড সার্ভার ২৪ ঘণ্টা চালু থাকবে।
- পৃথিবীর যেকোনো ওয়াইফাই, মোবাইল ডাটা এবং যেকোনো ডিভাইস (Android, iPhone, Mac, Windows) থেকে নিরাপদে HTTPS সিকিউর লিঙ্কে ব্রাউজ করা যাবে।

---

## 🏆 পদ্ধতি ১: Railway.app (সবচেয়ে সহজ এবং দ্রুততম — রিকমেন্ডেড)

Railway-তে Spring Boot এবং MySQL দুটিই ১-ক্লিকে সেটআপ করা যায়।

### ধাপ ১: প্রোজেক্টটি GitHub-এ আপলোড করুন
1. [github.com/new](https://github.com/new)-এ যান।
2. Repository name দিন: `Pets-Care-Management-System` এবং **Create repository**-তে ক্লিক করুন।
3. আপনার টার্মিনালে নিচের কমান্ড দুটি দিন (আপনার GitHub ইউজারনেম অনুযায়ী):
   ```bash
   git remote add origin https://github.com/tanzim557/Pets-Care-Management-System.git
   git push -u origin main
   ```
   *(অথবা আপনার পিসির **GitHub Desktop** অ্যাপ খুলে এই ফোল্ডারটি অ্যাড করে **Publish repository** ক্লিক করতে পারেন)*

### ধাপ ২: Railway-তে MySQL ডেটাবেস যুক্ত করুন
1. [railway.com](https://railway.com)-এ যান এবং **Login with GitHub** দিয়ে লগইন করুন।
2. **Dashboard** থেকে **New Project**-এ ক্লিক করুন।
3. **Provision MySQL** সিলেক্ট করুন। (কয়েক সেকেন্ডে ক্লাউড MySQL তৈরি হয়ে যাবে)।

### ধাপ ৩: Spring Boot অ্যাপটি কানেক্ট করুন
1. একই প্রোজেক্টে **+ New** বাটনে ক্লিক করে **GitHub Repo** সিলেক্ট করুন।
2. আপনার `Pets-Care-Management-System` রিপোজিটরিটি সিলেক্ট করুন।
3. অ্যাপ সার্ভিসের **Variables** ট্যাবে গিয়ে নিচের ৩টি ভেরিয়েবল অ্যাড করুন:
   - `SPRING_DATASOURCE_URL` = `jdbc:mysql://${{MySQL.MYSQLHOST}}:${{MySQL.MYSQLPORT}}/${{MySQL.MYSQLDATABASE}}?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC`
   - `SPRING_DATASOURCE_USERNAME` = `${{MySQL.MYSQLUSER}}`
   - `SPRING_DATASOURCE_PASSWORD` = `${{MySQL.MYSQLPASSWORD}}`
4. **Settings** ট্যাবে যান এবং **Networking** সেকশনে **Generate Domain**-এ ক্লিক করুন।
5. ব্যস! Railway আপনাকে একটি লাইভ সিকিউর লিঙ্ক দেবে (যেমন `https://petscare-production.up.railway.app`)।

---

## 🌟 পদ্ধতি ২: Render.com + TiDB Cloud (১০০% আজীবন ফ্রি — কোনো ক্রেডিট কার্ড লাগে না)

### ধাপ ১: TiDB Cloud-এ ফ্রি MySQL তৈরি করুন
1. [tidbcloud.com](https://tidbcloud.com)-এ যান এবং ফ্রিতে সাইনআপ করুন (Google দিয়ে লগইন)।
2. **Create Cluster** ক্লিক করুন -> **Serverless (Free)** সিলেক্ট করুন (এটি আজীবন সম্পূর্ণ ফ্রি)।
3. ক্লাস্টার তৈরি হলে **Connect** বাটনে ক্লিক করুন।
4. সেখানে আপনি Host, Port, Username, Password এবং JDBC URL পেয়ে যাবেন।

### ধাপ ২: Render.com-এ Spring Boot লাইভ করুন
1. [render.com](https://render.com)-এ যান এবং GitHub দিয়ে সাইন ইন করুন।
2. **New +** -> **Web Service** সিলেক্ট করুন।
3. আপনার `Pets-Care-Management-System` রিপোজিটরি কানেক্ট করুন।
4. **Language**: `Docker` সিলেক্ট করুন।
5. **Instance Type**: `Free` সিলেক্ট করুন।
6. **Environment Variables**-এ যোগ করুন:
   - `SPRING_DATASOURCE_URL`: আপনার TiDB-র JDBC URL
   - `SPRING_DATASOURCE_USERNAME`: আপনার TiDB ইউজারনেম
   - `SPRING_DATASOURCE_PASSWORD`: আপনার TiDB পাসওয়ার্ড
7. **Create Web Service** ক্লিক করুন!
8. ২ মিনিটের মধ্যে আপনার পার্মানেন্ট লাইভ ওয়েবসাইট লিংক রেডি হয়ে যাবে: `https://pets-care-hospital.onrender.com`!

---

## 🔑 ক্লাউড লাইভ সার্ভারের লগইন তথ্য (অটো-ইনিশিয়ালাইজড)
ক্লাউডে প্রথমবার ডেটাবেস রান করলে নিচের অ্যাকাউন্টগুলো অটোমেটিক তৈরি হয়ে যাবে:

| রোল (Role) | ইউজারনেম (Username) | পাসওয়ার্ড (Password) |
| :--- | :--- | :--- |
| **অ্যাডমিন (Admin)** | `admin` | `admin123` |
| **ডাক্তার (Doctor)** | `rupa` | `1234` |
| **রেসকিউ টিম (Rescue Team)** | `rescue1` | `1234` |
| **কাস্টমার (Customer)** | `customer1` | `pass123` |
