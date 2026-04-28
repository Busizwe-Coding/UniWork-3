# UniWork-3
Creating a budgeting app to record daily transactions 

## Overview
A simple budget tracking app that helps users manage daily expenses, set spending goals, and track spending across different categories. All data is stored locally using Room Database.

YOUTUBE DEMONSTRATION VIDEO: https://www.youtube.com/watch?v=77Yrbc4V1IE

## Features

### 🔐 User Authentication
- Simple login system with username and password validation
- Error handling for invalid credentials

### 📂 Category Management
- Create custom expense categories (e.g., Food, Transport, Shopping)
- Delete unwanted categories
- View all categories in a list

### 💰 Expense Tracking
- Add daily expenses with:
  - Date (date picker)
  - Start and end times (time picker)
  - Description
  - Amount
  - Category selection
  - Optional photo attachment
- All fields validated before saving
- Duplicate/empty input protection

### 🎯 Budget Goals
- Set minimum monthly spending goal
- Set maximum monthly spending limit
- Goals saved and persisted in database
- Input validation (min must be ≤ max, both non-negative)

### 📊 Reports & Analytics
- View expenses for any date range
- See total spending for selected period
- Category-wise spending breakdown
- Access photos attached to expenses
- Filter by custom date ranges

### 📸 Photo Support
- Take photos directly from the app
- Attach photos to expense entries
- View photos from report list
- Photos stored locally

## Tech Stack

- **Language:** Kotlin
- **Database:** Room Database (SQLite)
- **UI:** XML layouts with Material Design components
- **Camera:** CameraX library
- **Image Loading:** Glide
- **Architecture:** MVVM pattern with Coroutines

## Installation

1. Clone the repository
2. Open project in Android Studio (Ladybug or newer)
3. Sync Gradle files
4. Run on emulator or physical device (Android 6.0+)

## Database Schema

### Categories Table
- `id` (Primary Key)
- `name` (Category name)

### Expenses Table
- `id` (Primary Key)
- `date` (Expense date)
- `startTime` (Start time)
- `endTime` (End time)
- `description` (Expense description)
- `amount` (Money spent)
- `categoryId` (Foreign key to Categories)
- `photoPath` (Optional photo location)

### Budget Goals Table
- `id` (Primary Key)
- `minGoal` (Minimum monthly goal)
- `maxGoal` (Maximum monthly goal)

## How to Use

### First Time Setup
1. Login with any username/password (demo authentication)
2. Create budget categories (e.g., "Groceries", "Rent", "Entertainment")
3. Set your monthly budget goals (min and max)

### Recording Expenses Daily
1. Click "Add Expense"
2. Select date (defaults to current date)
3. Enter start and end times
4. Add description of the expense
5. Enter amount spent
6. Choose appropriate category
7. Optional: Take a photo of receipt
8. Click "Save Expense"

### Viewing Reports
1. Click "View Reports"
2. Select start and end dates
3. Click "Filter"
4. View:
   - List of all expenses for the period
   - Total amount spent
   - Category breakdown with totals
   - Photos (click photo button to view)

### Managing Budget Goals
- Update goals anytime from dashboard
- Goals saved automatically
- Helps track if you're overspending or underspending

## Error Handling

The app gracefully handles:
- Empty required fields
- Invalid numeric inputs
- Date/time format errors
- Camera permission denials
- Missing photo files
- Database errors

All errors display user-friendly messages without crashing.

## Data Persistence

- All data saved locally using Room Database
- No internet connection required
- Photos stored in device's external storage
- Data persists across app restarts

## Permissions Required

- **Camera**: For taking expense photos
- **Storage**: For saving and retrieving photos

## Known Minor Issues

- Photos are stored at full resolution (may take storage space)
- Category deletion doesn't check if expenses exist (will be fixed in next version)
- Date format is fixed to YYYY-MM-DD

## Future Improvements

- Export reports to CSV/PDF
- Charts and graphs for spending patterns
- Recurring expenses
- Budget alerts when approaching limits
- Multiple user profiles
- Cloud backup option
- Search and filter expenses

## Version
1.0.0 - Initial Release

## Requirements
- Android 6.0 (API 23) or higher
- Camera (optional - app works without it)
- 50MB free storage space
