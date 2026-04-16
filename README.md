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

### ✨ Key Features (v1.4.0)

* ☁️ **Cloud-Driven Visuals:** Integrated a **Dynamic Emoji System** managed via Firebase Firestore.
  Icons are updated remotely in real-time without needing an app update.
* 📊 **Smart Inventory Steppers:** New `ProductCard` UI with intelligent `+` and `-` buttons. The app
  automatically calculates increments based on units (e.g., **100g** for weights, **1 unit** for
  items).
* ⚡ **Zero-Friction Reset:** Long-press the minus button to instantly set stock to zero—perfect for
  items that just ran out.
* ✏️ **Quick-Edit Dialog:** Tap the stock number to open a dedicated adjustment overlay. Features *
  *Auto-Focus** and **Smart Text Selection** for lightning-fast manual entries.
* 🏠 **Collaborative Households:** Share your pantry using **6-character invite codes**. Optimized
  join flow with real-time feedback and clear error states.
* 👤 **Profile Personalization:** Integrated **Firebase Storage** for custom profile pictures with a
  modern management menu.
* 📐 **UX Refinement:** Support for **2-line product names** and enhanced visual hierarchy. Controls
  automatically disable during multi-selection mode to prevent accidental edits.
* 🔐 **Secure Auth:** Integrated with **Google Sign-In (Credential Manager)** and Email/Password.

### 🛠️ Tech Stack

* **Language:** Kotlin + Coroutines & Flow.
* **UI:** Jetpack Compose (Material Design 3).
* **Image Loading:** Coil for asynchronous images.
* **Backend:** Firebase (Firestore, Authentication & Storage).
* **Architecture:** MVVM (Model-View-ViewModel).

---

## Español

**Stock Master** es una aplicación de Android construida con **Jetpack Compose** diseñada
para la gestión colaborativa de inventarios hogareños. Permite a familias o compañeros de casa
sincronizar su despensa en tiempo real, gestionar listas de compras y controlar niveles de stock con
una interfaz moderna y elegante.

### ✨ Características (v1.4.0)

* ☁️ **Visuales desde la Nube:** Sistema de **Iconos Dinámicos** gestionado vía Firebase Firestore.
  Los emojis se actualizan de forma remota y en tiempo real sin actualizar la app.
* 📊 **Controles de Stock Inteligentes:** Nueva interfaz en `ProductCard` con botones `+` y `-`. La
  app calcula automáticamente el salto según la unidad (ej: **100g** para pesos, **1 unidad** para
  productos sueltos).
* ⚡ **Reseteo Instantáneo:** Mantén presionado el botón de restar para poner el stock en cero
  inmediatamente cuando un producto se termina.
* ✏️ **Edición Rápida de Precisión:** Toca el número de stock para abrir un diálogo de ajuste.
  Incluye **Auto-Foco** y **Selección Automática** para que solo tengas que tocar y escribir.
* 🏠 **Hogares Colaborativos:** Compartí tu despensa usando **códigos de invitación de 6 caracteres
  **. Flujo de unión optimizado con estados de carga claros.
* 👤 **Personalización de Perfil:** Integración con **Firebase Storage** para subir fotos de perfil y
  gestión de identidad moderna.
* 📐 **Refinamiento de UX:** Soporte para **nombres de 2 líneas** y jerarquía visual mejorada. Los
  controles se bloquean durante la selección múltiple para evitar errores.
* 🔐 **Acceso Seguro:** Integración con **Google Sign-In (Credential Manager)** y Email para un ingreso rápido.

---

### 📦 Installation / Instalación

You can download the latest APK from the releases section / Podés descargar el último APK desde la
sección de releases:
👉 [**Download Stock Master v1.4.0
**](https://github.com/gcendon91/StockMaster/releases/download/v1.4.0/StockMaster_v1.4.0.apk)

---
*Developed by Gonzalo Cendón - 2026*