package com.sentinelbet.app.data.db;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Long;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class BetDao_Impl implements BetDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<BetEntity> __insertionAdapterOfBetEntity;

  private final BetResultConverter __betResultConverter = new BetResultConverter();

  private final EntityDeletionOrUpdateAdapter<BetEntity> __updateAdapterOfBetEntity;

  private final SharedSQLiteStatement __preparedStmtOfDeleteById;

  public BetDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfBetEntity = new EntityInsertionAdapter<BetEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `bets` (`id`,`match`,`market`,`odd`,`stake`,`result`,`profit`,`date`,`notes`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final BetEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getMatch());
        statement.bindString(3, entity.getMarket());
        statement.bindDouble(4, entity.getOdd());
        statement.bindDouble(5, entity.getStake());
        final String _tmp = __betResultConverter.toString(entity.getResult());
        statement.bindString(6, _tmp);
        statement.bindDouble(7, entity.getProfit());
        statement.bindString(8, entity.getDate());
        if (entity.getNotes() == null) {
          statement.bindNull(9);
        } else {
          statement.bindString(9, entity.getNotes());
        }
      }
    };
    this.__updateAdapterOfBetEntity = new EntityDeletionOrUpdateAdapter<BetEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `bets` SET `id` = ?,`match` = ?,`market` = ?,`odd` = ?,`stake` = ?,`result` = ?,`profit` = ?,`date` = ?,`notes` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final BetEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getMatch());
        statement.bindString(3, entity.getMarket());
        statement.bindDouble(4, entity.getOdd());
        statement.bindDouble(5, entity.getStake());
        final String _tmp = __betResultConverter.toString(entity.getResult());
        statement.bindString(6, _tmp);
        statement.bindDouble(7, entity.getProfit());
        statement.bindString(8, entity.getDate());
        if (entity.getNotes() == null) {
          statement.bindNull(9);
        } else {
          statement.bindString(9, entity.getNotes());
        }
        statement.bindLong(10, entity.getId());
      }
    };
    this.__preparedStmtOfDeleteById = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM bets WHERE id = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insert(final BetEntity bet, final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfBetEntity.insertAndReturnId(bet);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object update(final BetEntity bet, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfBetEntity.handle(bet);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteById(final int id, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteById.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, id);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfDeleteById.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<BetEntity>> observeAll() {
    final String _sql = "SELECT * FROM bets ORDER BY date ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"bets"}, new Callable<List<BetEntity>>() {
      @Override
      @NonNull
      public List<BetEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfMatch = CursorUtil.getColumnIndexOrThrow(_cursor, "match");
          final int _cursorIndexOfMarket = CursorUtil.getColumnIndexOrThrow(_cursor, "market");
          final int _cursorIndexOfOdd = CursorUtil.getColumnIndexOrThrow(_cursor, "odd");
          final int _cursorIndexOfStake = CursorUtil.getColumnIndexOrThrow(_cursor, "stake");
          final int _cursorIndexOfResult = CursorUtil.getColumnIndexOrThrow(_cursor, "result");
          final int _cursorIndexOfProfit = CursorUtil.getColumnIndexOrThrow(_cursor, "profit");
          final int _cursorIndexOfDate = CursorUtil.getColumnIndexOrThrow(_cursor, "date");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final List<BetEntity> _result = new ArrayList<BetEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final BetEntity _item;
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            final String _tmpMatch;
            _tmpMatch = _cursor.getString(_cursorIndexOfMatch);
            final String _tmpMarket;
            _tmpMarket = _cursor.getString(_cursorIndexOfMarket);
            final double _tmpOdd;
            _tmpOdd = _cursor.getDouble(_cursorIndexOfOdd);
            final double _tmpStake;
            _tmpStake = _cursor.getDouble(_cursorIndexOfStake);
            final BetResult _tmpResult;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfResult);
            _tmpResult = __betResultConverter.fromString(_tmp);
            final double _tmpProfit;
            _tmpProfit = _cursor.getDouble(_cursorIndexOfProfit);
            final String _tmpDate;
            _tmpDate = _cursor.getString(_cursorIndexOfDate);
            final String _tmpNotes;
            if (_cursor.isNull(_cursorIndexOfNotes)) {
              _tmpNotes = null;
            } else {
              _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
            }
            _item = new BetEntity(_tmpId,_tmpMatch,_tmpMarket,_tmpOdd,_tmpStake,_tmpResult,_tmpProfit,_tmpDate,_tmpNotes);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object getAll(final Continuation<? super List<BetEntity>> $completion) {
    final String _sql = "SELECT * FROM bets ORDER BY date ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<BetEntity>>() {
      @Override
      @NonNull
      public List<BetEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfMatch = CursorUtil.getColumnIndexOrThrow(_cursor, "match");
          final int _cursorIndexOfMarket = CursorUtil.getColumnIndexOrThrow(_cursor, "market");
          final int _cursorIndexOfOdd = CursorUtil.getColumnIndexOrThrow(_cursor, "odd");
          final int _cursorIndexOfStake = CursorUtil.getColumnIndexOrThrow(_cursor, "stake");
          final int _cursorIndexOfResult = CursorUtil.getColumnIndexOrThrow(_cursor, "result");
          final int _cursorIndexOfProfit = CursorUtil.getColumnIndexOrThrow(_cursor, "profit");
          final int _cursorIndexOfDate = CursorUtil.getColumnIndexOrThrow(_cursor, "date");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final List<BetEntity> _result = new ArrayList<BetEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final BetEntity _item;
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            final String _tmpMatch;
            _tmpMatch = _cursor.getString(_cursorIndexOfMatch);
            final String _tmpMarket;
            _tmpMarket = _cursor.getString(_cursorIndexOfMarket);
            final double _tmpOdd;
            _tmpOdd = _cursor.getDouble(_cursorIndexOfOdd);
            final double _tmpStake;
            _tmpStake = _cursor.getDouble(_cursorIndexOfStake);
            final BetResult _tmpResult;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfResult);
            _tmpResult = __betResultConverter.fromString(_tmp);
            final double _tmpProfit;
            _tmpProfit = _cursor.getDouble(_cursorIndexOfProfit);
            final String _tmpDate;
            _tmpDate = _cursor.getString(_cursorIndexOfDate);
            final String _tmpNotes;
            if (_cursor.isNull(_cursorIndexOfNotes)) {
              _tmpNotes = null;
            } else {
              _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
            }
            _item = new BetEntity(_tmpId,_tmpMatch,_tmpMarket,_tmpOdd,_tmpStake,_tmpResult,_tmpProfit,_tmpDate,_tmpNotes);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
