package com.example.financeapp.data.model

enum class Category(val title: String) {
    FOOD("Продукты"),
    TRANSPORT("Транспорт"),
    SHOPPING("Покупки"),
    ENTERTAINMENT("Развлечения"),
    HEALTH("Здоровье"),
    HOUSING("Жилье"),
    CAFE("Кафе и рестораны"),
    EDUCATION("Образование"),
    SALARY("Зарплата"),
    TRANSFER("Переводы"),
    OTHER("Другое");

    companion object {
        fun fromTitle(title: String): Category {
            return values().find { it.title == title } ?: OTHER
        }
    }
} 