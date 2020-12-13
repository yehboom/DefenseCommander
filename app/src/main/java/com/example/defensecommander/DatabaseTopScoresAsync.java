package com.example.defensecommander;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class DatabaseTopScoresAsync extends AsyncTask<String, Void, String> {

    private static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";

    @SuppressLint("StaticFieldLeak")
    private ResultActivity context;
    private static String dbURL;
    private Connection conn;
    private static final String TAG = "DatabaseTopScoresAsync";
    private static final String AppScores = "AppScores";
    private SimpleDateFormat sdf = new SimpleDateFormat("MM/dd HH:mm", Locale.getDefault());
    private int count;
    private int scoreValue;
    AlertDialog.Builder builder;
    AlertDialog AlertDialog;
    private String userInput;
    private  String level;
    private String name;
    private String lastScore;


    DatabaseTopScoresAsync(ResultActivity ctx) {
        context = ctx;
        dbURL = "jdbc:mysql://christopherhield.com:3306/chri5558_missile_defense";
    }


    protected void onPreExecute() {
        super.onPreExecute();
        builder = new AlertDialog.Builder(this.context);
    }

    protected String doInBackground(String... values) {

        String temps = values[0];
        scoreValue=Integer.parseInt(temps);
        level = values[1];
        name = values[2];

        try {
            Class.forName(JDBC_DRIVER);

            conn = DriverManager.getConnection(dbURL, "chri5558_student", "ABC.123");

            StringBuilder sb = new StringBuilder();
            if(name!=""){
              String response = addScore();
            }

            sb.append(getAll());
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }


    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

        Log.d(TAG, "doInBackground2: " + s);
        context.setResults(s);


        if(name == "" && (scoreValue>Integer.parseInt(lastScore))) {

            final EditText et = new EditText(this.context);
            et.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
            et.setGravity(Gravity.CENTER_HORIZONTAL);

            builder.setView(et);
            et.setFilters(new InputFilter[]{
                    new InputFilter.LengthFilter(3)
            });

            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    userInput = et.getText().toString();
                    context.onResume();
                    context.setResult2(userInput);
                }
            });
            builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
               context.onResume();
                }
            });

            builder.setMessage("Please enter your initials(up to 3 characters):");
            builder.setTitle("You are a Top-Player!");

            AlertDialog dialog = builder.create();
            dialog.show();
        }

    }

    private String addScore() throws SQLException {
        Statement stmt = conn.createStatement();

        String sql = "insert into " + AppScores + " values (" +
                System.currentTimeMillis() + ", '" + name + "', " + scoreValue + ", " +level +
                ")";
        Log.d(TAG, "addScore: "+sql);

        int result = stmt.executeUpdate(sql);

        stmt.close();

        return " "+result;
    }


    private String getAll() throws SQLException {
        Statement stmt = conn.createStatement();

        String sql = "select * from " + AppScores + " ORDER BY Score DESC LIMIT 10";

        StringBuilder sb = new StringBuilder();


        ResultSet rs = stmt.executeQuery(sql);

        while (rs.next()) {
            long millis = rs.getLong(1);
            String name = rs.getString(2);
            int score = rs.getInt(3);
            int level = rs.getInt(4);
            count++;

          sb.append(String.format("%3d %7s %7d %7d %14s%n", count,name, level,score, sdf.format(new Date(millis))));


        }
        rs.last();
        int intlastScore = rs.getInt(3);
        lastScore=String.valueOf(intlastScore);

        rs.close();
        stmt.close();

        return sb.toString();
    }





}
