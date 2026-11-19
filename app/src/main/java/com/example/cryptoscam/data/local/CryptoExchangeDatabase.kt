package com.example.cryptoscam.data.local

import android.content.Context
import androidx.room3.Database
import androidx.room3.Room
import androidx.room3.RoomDatabase
import androidx.sqlite.driver.AndroidSQLiteDriver


// Вказуємо список усіх таблиць (entities) та версію бази даних
@Database(entities = [CryptoExchangeEntity::class], version = 2, exportSchema = false)
abstract class CryptoExchangeDatabase : RoomDatabase() {

    // Метод, через який ми отримуватимемо доступ до нашого DAO
    abstract fun cryptoExchangeDao(): CryptoExchangeDao

    companion object {
        // Запобігаємо створенню кількох екземплярів бази в пам'яті (Singleton)
        @Volatile
        private var INSTANCE: CryptoExchangeDatabase ? = null

        fun getDatabase(context: Context): CryptoExchangeDatabase  {
            return INSTANCE ?: synchronized(this) {
                // Подвійна перевірка (Double-checked locking) для потокобезпеки
                INSTANCE?.let { return it }

                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CryptoExchangeDatabase::class.java,
                    "crypto_exchange.db" // Назва фізичного файлу бази на диску
                )
                    .setDriver(AndroidSQLiteDriver())
                    .fallbackToDestructiveMigration(dropAllTables = true) // при зміні схеми: стерти й створити заново
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}
