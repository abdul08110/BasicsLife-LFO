package com.hasbro.basicslife_lfo;

import android.os.SystemClock;
import android.view.View;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;

public abstract class SafeClikcListener implements View.OnClickListener {
    protected int defaultInterval;
    private long lastTimeClicked = 0;

    public SafeClikcListener() {
        this(1000);
    }

    public SafeClikcListener(int minInterval) {
        this.defaultInterval = minInterval;
    }

    @Override
    public void onClick(View v) {
        if (SystemClock.elapsedRealtime() - lastTimeClicked < defaultInterval) {
            return;
        }
        lastTimeClicked = SystemClock.elapsedRealtime();
        try {
            performClick(v);
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public abstract void performClick(View v) throws ParseException, SQLException, IOException;

}

