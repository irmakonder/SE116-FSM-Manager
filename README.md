# 🤖 SE116 FSM Manager

A Java-based **Finite State Machine (FSM) simulator** that lets you define states and transitions, then simulate the machine step-by-step with user input.

---

## 📋 Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Project Structure](#project-structure)
- [How It Works](#how-it-works)
- [How to Run](#how-to-run)
- [Example](#example)

---

## 📌 Overview

SE116 FSM Manager is a console-based Java application that:
- Lets you define a **Finite State Machine** with custom states and transitions
- Accepts **user input** to simulate the FSM step by step
- Shows whether input sequences are **accepted or rejected** by the machine

---

## ✨ Features

- Define states (including start and accept states)
- Define transitions between states on given inputs
- Simulate the FSM interactively via user input
- Detect and report invalid transitions
- Clean OOP design with separate classes for each FSM component

---

## 🗂️ Project Structure

```
SE116-FSM-Manager/
│
├── State.java           # Represents a single FSM state
├── Transition.java      # Represents a transition between states
├── FSM.java             # Core FSM logic (states, transitions, simulation)
├── Main.java            # Entry point: user interaction and orchestration
└── ...                  # Additional helper classes if any
```

### Class Descriptions

| Class | Responsibility |
|-------|---------------|
| `State` | Stores state name and whether it's a start/accept state |
| `Transition` | Stores source state, input symbol, and target state |
| `FSM` | Manages the machine: holds states & transitions, runs simulation |
| `Main` | Handles user input and drives the program |

---

## ⚙️ How It Works

1. **States are defined** — each state has a name and is marked as start, accept, or regular.
2. **Transitions are defined** — each transition connects two states via an input symbol.
3. **User provides an input string** — the FSM processes it symbol by symbol.
4. **The machine simulates** — starting from the initial state, it follows transitions.
5. **Result is reported** — the input is either **accepted** (ends in accept state) or **rejected**.

---

## ▶️ How to Run

### 1. Compile

```bash
javac *.java
```

### 2. Run

```bash
java Main
```

### 3. Follow the prompts

```
Welcome to SE116 FSM Manager
Define your states, transitions, and input to simulate the FSM.
```

---

## 💡 Example

**A simple FSM that accepts strings ending in 'b':**

```
States: q0 (start), q1 (accept)
Transitions:
  q0 --a--> q0
  q0 --b--> q1
  q1 --a--> q0
  q1 --b--> q1

Input: "aab"
Result: ACCEPTED ✅

Input: "aba"
Result: REJECTED ❌
```

---

## 🔧 Notes

- The FSM is a **Deterministic Finite Automaton (DFA)** — exactly one transition per state/input pair.
- Invalid input symbols (no matching transition) cause the machine to reject immediately.
- Only one start state is allowed; multiple accept states are supported.

---

## 👩‍💻 Author

Developed as part of the **SE116** course project.
