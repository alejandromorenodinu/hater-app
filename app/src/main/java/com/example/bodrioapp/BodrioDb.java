package com.example.bodrioapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class BodrioDb extends SQLiteOpenHelper {
    // Esta clase es la clase que genera la base de datos de BodrioApp, con dos tablas: favoritos y valorados.
    public BodrioDb(Context context) {
        super(context, "bodrio.db", null, 1);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(
                "CREATE TABLE favoritos (" +
                        "id TEXT PRIMARY KEY, " +
                        "titulo TEXT)"
        );

        db.execSQL(
                "CREATE TABLE valorados (" +
                        "id TEXT PRIMARY KEY, " +
                        "valoracion INTEGER)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS favoritos");
        db.execSQL("DROP TABLE IF EXISTS valorados");
        onCreate(db);
    }
}
