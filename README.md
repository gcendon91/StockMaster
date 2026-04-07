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

### ✨ Key Features (v1.3.0)

* 👤 **Profile Personalization:** Integrated **Firebase Storage** to upload custom profile pictures.
  Manage your identity with a modern menu (Google Sync, Custom Upload, or Avatar removal).
* 🏠 **Collaborative Households:** Share your pantry using **6-character invite codes**. Optimized
  join flow with real-time feedback and clear error states.
* ⚡ **Real-time Sync:** Powered by **Firebase Firestore**, every change is updated instantly across all connected devices.
* 🛒 **Streamlined Shopping List:** Cleaned UI focusing on "Missing" amounts. Restored the classic
  large-text urgency with a modern "Slim Card" design and **Quick Purchase** buttons.
* 📦 **Infinite-Add Flow:** Quickly populate your inventory with an optimized `AddProductDialog` that
  stays open and auto-focuses for multiple consecutive entries.
* 📐 **Visual Consistency:** Unified design system across Home and Shopping List using a shared DNA:
  18dp corner radius, 3dp elevation, and standardized 8dp spacing.
* 🔐 **Secure Auth:** Integrated with **Google Sign-In (Credential Manager)** and Email/Password.
* 🍳 **Modern Aesthetics:** "Kitchen-themed" UI with glassmorphism, optimized dark-overlay visuals, and intuitive UX.

### 🛠️ Tech Stack

* **Language:** Kotlin + Coroutines & Flow.
* **UI:** Jetpack Compose (Material Design 3).
* **Image Loading:** Coil for asynchronous profile and product images.
* **Backend:** Firebase (Firestore, Authentication & Storage).
* **Architecture:** MVVM (Model-View-ViewModel).

---

## Español

**Stock Master** es una aplicación de Android construida con **Jetpack Compose** diseñada
para la gestión colaborativa de inventarios hogareños. Permite a familias o compañeros de casa
sincronizar su despensa en tiempo real, gestionar listas de compras y controlar niveles de stock con
una interfaz moderna y elegante.

### ✨ Características (v1.3.0)

* 👤 **Personalización de Perfil:** Integración con **Firebase Storage** para subir fotos de perfil.
  Gestioná tu identidad desde un menú moderno (Sincronización con Google, Carga personalizada o
  eliminar avatar).
* 🏠 **Hogares Colaborativos:** Compartí tu despensa usando **códigos de invitación de 6 caracteres
  **. Flujo de unión optimizado con estados de carga claros.
* ⚡ **Sincronización en Tiempo Real:** Gracias a **Firebase Firestore**, cada cambio se refleja al instante en todos los dispositivos.
* 🛒 **Lista de Compras Optimizada:** UI limpia enfocada en lo que falta comprar. Recuperamos la
  claridad visual del "Semáforo de Stock" con un diseño de tarjetas horizontales más eficiente.
* 📦 **Flujo de Carga Infinita:** Diálogo de "Agregar Producto" optimizado con auto-foco para
  permitir cargas rápidas y consecutivas del inventario sin cerrar la ventana.
* 📐 **Consistencia Visual:** Sistema de diseño unificado entre la Home y la Shopping List: radio de
  bordes de 18dp, elevaciones de 3dp y espaciado estándar de 8dp.
* 🔐 **Acceso Seguro:** Integración con **Google Sign-In (Credential Manager)** y Email para un ingreso rápido.
* 🍳 **Estética Moderna:** Interfaz con temática de cocina, efectos de "glassmorphism" y optimización visual de alto contraste para uso en ambientes luminosos (como el supermercado).

---

### 📦 Installation / Instalación

You can download the latest APK from the releases section / Podés descargar el último APK desde la
sección de releases:
👉 [**Download Stock Master v1.3.0
**](https://github.com/gcendon91/StockMaster/releases/download/v1.3.0/StockMaster_v1.3.0.apk)

---
*Developed by Gonzalo Cendón - 2026*