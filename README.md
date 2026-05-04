# Stock Master

[English](#english) | [Español](#español)

---

## English

**Stock Master** is a professional Android application built with **Jetpack Compose**, designed for
collaborative household inventory management. It allows real-time synchronization, smart shopping
list generation, and advanced stock tracking with a high-end, modern UI.

### 🚀 Google Play Status

> **Current Phase:** `Closed Testing (Beta)`  
> This project follows industry standards for distribution, including signed bundles (AAB),
> versioning control, and Google Play Console deployment.

<div align="center">
  <img src="https://raw.githubusercontent.com/gcendon91/StockMaster/master/screenshots/login_screen.png" width="30%" />
  <img src="https://raw.githubusercontent.com/gcendon91/StockMaster/master/screenshots/home_screen.png" width="30%" />
  <img src="https://raw.githubusercontent.com/gcendon91/StockMaster/master/screenshots/shopping_list.png" width="30%" />
</div>

### ✨ Key Features (v1.4.0)

* 📐 **UX Refinement & Smart Logic:** Features **Smart Category Sorting** (ensuring "Others" stays at
  the bottom while keeping the rest alphabetical) and support for 2-line product names for better
  readability.
* ☁️ **Cloud-Driven Visuals:** Integrated a **Dynamic Emoji System** managed via Firebase Firestore.
  Icons update remotely without requiring an app update.
* 📊 **Smart Inventory Steppers:** `ProductCard` UI with intelligent increments (e.g., **100g** for
  weights, **1 unit** for items).
* ⚡ **Zero-Friction Reset:** Long-press the minus button to instantly set stock to zero.
* ✏️ **Quick-Edit Dialog:** Tap stock numbers for a precision adjustment overlay with **Auto-Focus**
  and **Smart Text Selection**.
* 🏠 **Collaborative Households:** Share pantries via **6-character invite codes** with real-time
  feedback.
* 👤 **Profile Personalization:** **Firebase Storage** integration for custom profile pictures.
* 🔐 **Secure Auth:** Integrated with **Google Sign-In (Credential Manager)** and Email/Password.

### 🛠️ Tech Stack

* **Language:** Kotlin + Coroutines & Flow.
* **UI:** Jetpack Compose (Material Design 3).
* **Architecture:** MVVM (Model-View-ViewModel).
* **Backend:** Firebase (Firestore, Auth & Storage).
* **Image Loading:** Coil.

---

## Español

**Stock Master** es una aplicación profesional de Android construida con **Jetpack Compose** para la
gestión colaborativa de inventarios. Sincronización en tiempo real, listas de compras inteligentes y
una interfaz moderna de alto nivel.

### 🚀 Estado en Google Play

> **Fase Actual:** `Pruebas Cerradas (Beta)`  
> El proyecto cumple con los estándares de la industria para distribución: Bundles firmados (AAB),
> control de versiones y gestión profesional en Google Play Console.

### ✨ Características (v1.4.0)

* 📐 **Refinamiento de UX y Lógica:** Implementación de **Ordenamiento Inteligente de Categorías** (
  mantiene "Otros" al final y el resto alfabético) y soporte para nombres largos de productos.
* ☁️ **Visuales desde la Nube:** Sistema de **Iconos Dinámicos** vía Firestore. Emojis actualizables
  de forma remota sin subir nuevas versiones.
* 📊 **Controles Inteligentes:** Interfaz con saltos automáticos según la unidad (ej: **100g** o **1
  unidad**).
* ⚡ **Reseteo Instantáneo:** Mantén presionado "-" para vaciar el stock de un producto
  inmediatamente.
* ✏️ **Edición Rápida:** Diálogo de ajuste con **Auto-Foco** y **Selección Automática** para
  entradas rápidas.
* 🏠 **Hogares Colaborativos:** Sincronización mediante **códigos de invitación de 6 caracteres**.
* 👤 **Personalización de Perfil:** Uso de **Firebase Storage** para fotos de perfil personalizadas.
* 🔐 **Acceso Seguro:** Integración con **Google Sign-In** y Email.

---

### 📦 Installation / Instalación

> 🧪 **Beta Testing:** This app is currently in a closed beta on Google Play. If you'd like to join
> the testing team, please contact me via LinkedIn or GitHub!
>
> Alternatively, you can download the latest manual build:  
> 👉 [**Download Stock Master v1.5.1 (APK)
**](https://github.com/gcendon91/StockMaster/releases/download/v1.5.1/StockMaster_v1.5.1.apk)

---
*Developed by Gonzalo Cendón - 2026*