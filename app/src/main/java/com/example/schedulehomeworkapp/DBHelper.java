package com.example.schedulehomeworkapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.schedulehomeworkapp.db.Discipline;
import com.example.schedulehomeworkapp.db.LessonItem;
import com.example.schedulehomeworkapp.db.Task;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class DBHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "schedule_app.db";
    private static final int DB_VERSION = 1;

    public static final String TABLE_DISCIPLINE = "disciplines";
    public static final String TABLE_LESSON = "lessons";
    public static final String TABLE_TASK = "tasks";

    public static final String D_ID = "id";
    public static final String D_NAME = "name";
    public static final String D_TEACHER = "teacher";
    public static final String D_COLOR = "color";

    public static final String L_ID = "id";
    public static final String L_DISCIPLINE_ID = "discipline_id";
    public static final String L_DAY_OF_WEEK = "day_of_week";
    public static final String L_START = "start_time";
    public static final String L_END = "end_time";
    public static final String L_ROOM = "room";
    public static final String L_WEEK_TYPE = "week_type";

    public static final String T_ID = "id";
    public static final String T_TITLE = "title";
    public static final String T_DISCIPLINE_ID = "discipline_id";
    public static final String T_DEADLINE = "deadline";
    public static final String T_PRIORITY = "priority";
    public static final String T_DONE = "done";

    public DBHelper(Context ctx){ super(ctx, DB_NAME, null, DB_VERSION); }

    @Override public void onCreate(SQLiteDatabase db){
        String createDisc = "CREATE TABLE "+TABLE_DISCIPLINE+" ("+D_ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+D_NAME+" TEXT, "+D_TEACHER+" TEXT, "+D_COLOR+" INTEGER);";
        String createLesson = "CREATE TABLE "+TABLE_LESSON+" ("+L_ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+L_DISCIPLINE_ID+" INTEGER, "+L_DAY_OF_WEEK+" INTEGER, "+L_START+" TEXT, "+L_END+" TEXT, "+L_ROOM+" TEXT, "+L_WEEK_TYPE+" INTEGER);";
        String createTask = "CREATE TABLE "+TABLE_TASK+" ("+T_ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+T_TITLE+" TEXT, "+T_DISCIPLINE_ID+" INTEGER, "+T_DEADLINE+" TEXT, "+T_PRIORITY+" INTEGER, "+T_DONE+" INTEGER DEFAULT 0);";
        db.execSQL(createDisc); db.execSQL(createLesson); db.execSQL(createTask);

    }

    @Override public void onUpgrade(SQLiteDatabase db, int oldV, int newV){
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_TASK);
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_LESSON);
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_DISCIPLINE);
        onCreate(db);
    }

    public long addDiscipline(String name, String teacher, int color){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put(D_NAME, name); v.put(D_TEACHER, teacher); v.put(D_COLOR, color);
        return db.insert(TABLE_DISCIPLINE, null, v);
    }
    public List<Discipline> getAllDisciplines(){
        List<Discipline> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(TABLE_DISCIPLINE, null, null, null, null, null, D_NAME+" ASC");
        while(c.moveToNext()){
            Discipline d = new Discipline();
            d.id = c.getLong(c.getColumnIndexOrThrow(D_ID));
            d.name = c.getString(c.getColumnIndexOrThrow(D_NAME));
            d.teacher = c.getString(c.getColumnIndexOrThrow(D_TEACHER));
            d.color = c.getInt(c.getColumnIndexOrThrow(D_COLOR));
            list.add(d);
        }
        c.close();
        return list;
    }

    public List<LessonItem> getLessonsForDay(int dayOfWeek, int currentCycleWeeksMode, int selectedWeek){
        List<LessonItem> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        String q = "SELECT l."+L_ID+", l."+L_START+", l."+L_END+", l."+L_ROOM+", l."+L_WEEK_TYPE+", d."+D_NAME+", d."+D_COLOR+" FROM "+TABLE_LESSON+" l LEFT JOIN "+TABLE_DISCIPLINE+" d ON l."+L_DISCIPLINE_ID+" = d."+D_ID+" WHERE l."+L_DAY_OF_WEEK+"=? ORDER BY l."+L_START+" ASC";
        Cursor c = db.rawQuery(q, new String[]{String.valueOf(dayOfWeek)});
        while(c.moveToNext()){
            int weekType = c.getInt(c.getColumnIndexOrThrow(L_WEEK_TYPE));
            boolean include = false;
            if (currentCycleWeeksMode <= 1) include = true;
            else {
                if (weekType == 0) include = true;
                else if (weekType == selectedWeek) include = true;
            }
            if (!include) continue;
            LessonItem it = new LessonItem();
            it.id = c.getLong(0);
            it.start = c.getString(1);
            it.end = c.getString(2);
            it.room = c.getString(3);
            it.disciplineName = c.getString(5);
            it.color = c.getInt(6);
            list.add(it);
        }
        c.close();
        return list;
    }

    public long addTask(String title, Long disciplineId, String deadlineIso, int priority){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put(T_TITLE, title);
        if (disciplineId!=null) v.put(T_DISCIPLINE_ID, disciplineId);
        v.put(T_DEADLINE, deadlineIso);
        v.put(T_PRIORITY, priority);
        return db.insert(TABLE_TASK, null, v);
    }
    public List<Task> getTasksFiltered(long disciplineIdFilter){
        List<Task> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        String q = "SELECT t."+T_ID+", t."+T_TITLE+", t."+T_DISCIPLINE_ID+", t."+T_DEADLINE+", t."+T_PRIORITY+", t."+T_DONE+", d."+D_NAME+" FROM "+TABLE_TASK+" t LEFT JOIN "+TABLE_DISCIPLINE+" d ON t."+T_DISCIPLINE_ID+" = d."+D_ID+" ORDER BY t."+T_DONE+" ASC, t."+T_DEADLINE+" ASC";
        Cursor c = db.rawQuery(q, null);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());
        while(c.moveToNext()){
            long did = c.getLong(2);
            if (disciplineIdFilter != -1 && disciplineIdFilter != did) continue;
            Task t = new Task();
            t.id = c.getLong(0);
            t.title = c.getString(1);
            t.disciplineId = did;
            String dl = c.getString(3);
            try{ if (dl != null) t.deadline = df.parse(dl); } catch(Exception ignored){}
            t.priority = c.getInt(4);
            t.done = c.getInt(5) == 1;
            t.disciplineName = c.getString(6);
            list.add(t);
        }
        c.close();
        Collections.sort(list, (a,b)->{
            if (a.done != b.done) return a.done ? 1 : -1;
            if (a.deadline == null && b.deadline == null) return 0;
            if (a.deadline == null) return 1;
            if (b.deadline == null) return -1;
            return a.deadline.compareTo(b.deadline);
        });
        return list;
    }


// Lessons CRUD
public long addLesson(Long disciplineId, int dayOfWeek, String startIso, String endIso, String room, int weekType){
    SQLiteDatabase db = getWritableDatabase();
    ContentValues v = new ContentValues();
    if (disciplineId!=null) v.put(L_DISCIPLINE_ID, disciplineId);
    v.put(L_DAY_OF_WEEK, dayOfWeek);
    v.put(L_START, startIso);
    v.put(L_END, endIso);
    if (room!=null) v.put(L_ROOM, room);
    v.put(L_WEEK_TYPE, weekType);
    return db.insert(TABLE_LESSON, null, v);
}
    public void deleteLesson(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("lessons", "id=?", new String[]{String.valueOf(id)});
        db.close();
    }
    public void deleteTask(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("tasks", "id=?", new String[]{String.valueOf(id)});
        db.close();
    }
}