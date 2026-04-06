# Stock Master

[English](#english) | [Español](#español)

---

## English

**Stock Master** is an Android application built with **Jetpack Compose** designed for
collaborative household inventory management. It allows families or roommates to sync their pantry
in real-time, manage shopping lists, and track stock levels with a sleek, modern UI.

<div align="center">
  <img src="https://raw.githubusercontent.com/gcendon91/StockMaster/master/screenshots/login_screen.png" width="30%" />
  <img src="https://raw.githubusercontent.com/gcendon91/StockMaster/master/screenshots/home_screen.png" width="30%" />
  <img src="https://raw.githubusercontent.com/gcendon91/StockMaster/master/screenshots/shopping_list.png" width="30%" />
</div>

### ✨ Key Features (v1.2)

* 🏠 **Collaborative Households:** Share your pantry using **reliable 6-character invite codes**. Optimized join flow with real-time feedback.
* ⚡ **Real-time Sync:** Powered by **Firebase Firestore**, every change is updated instantly across all connected devices.
* 🚦 **Dynamic Stock Semaphore:** Refined visual alerts to prioritize your shopping:
    * 🔴 **Red:** Critical (less than 10% of minimum).
    * 🟡 **Amber:** Needs restock (below minimum level).
    * 🟢 **Green:** Healthy stock (ideal level reached).
* 🛒 **Smart Shopping List & Quick Restock:** Automatically tracks missing items and includes a **Quick Purchase Dialog** to update stock manually or using suggested amounts while you shop.
* 🔐 **Secure Auth:** Integrated with **Google Sign-In (Credential Manager)** and Email/Password.
* 🍳 **Modern Aesthetics:** "Kitchen-themed" UI with glassmorphism, optimized dark-overlay visuals, and intuitive UX.

### 🛠️ Tech Stack

* **Language:** Kotlin + Coroutines & Flow.
* **UI:** Jetpack Compose (Material Design 3).
* **Backend:** Firebase (Firestore & Authentication).
* **Auth:** Google Credential Manager API.
* **Architecture:** MVVM (Model-View-ViewModel).

---

## Español

**Stock Master** es una aplicación de Android construida con **Jetpack Compose** diseñada
para la gestión colaborativa de inventarios hogareños. Permite a familias o compañeros de casa
sincronizar su despensa en tiempo real, gestionar listas de compras y controlar niveles de stock con
una interfaz moderna y elegante.

### ✨ Características (v1.2)

* 🏠 **Hogares Colaborativos:** Compartí tu despensa usando **códigos de invitación de 6 caracteres**. Flujo de unión optimizado con estados de carga y errores claros.
* ⚡ **Sincronización en Tiempo Real:** Gracias a **Firebase Firestore**, cada cambio se refleja al instante en todos los dispositivos.
* 🚦 **Semáforo de Stock Dinámico:** Alertas visuales refinadas para priorizar tus compras:
    * 🔴 **Rojo:** Crítico (menos del 10% del mínimo).
    * 🟡 **Ámbar:** Falta stock (por debajo del nivel mínimo).
    * 🟢 **Verde:** Stock ideal.
* 🛒 **Lista de Compras e Incremento Rápido:** Calcula qué falta y permite **reponer stock al instante** mediante un diálogo de compra que sugiere la cantidad faltante.
* 🔐 **Acceso Seguro:** Integración con **Google Sign-In (Credential Manager)** y Email para un ingreso rápido.
* 🍳 **Estética Moderna:** Interfaz con temática de cocina, efectos de "glassmorphism" y optimización visual de alto contraste para uso en ambientes luminosos (como el supermercado).

---

### 📦 Installation / Instalación

You can download the latest APK from the releases section / Podés descargar el último APK desde la
sección de releases:
👉 [**Download Stock Master v1.2.0**](https://github.com/gcendon91/StockMaster/releases/download/v1.2.0/StockMaster_v1.2.0.apk)

---
*Developed by Gonzalo Cendón - 2026*
