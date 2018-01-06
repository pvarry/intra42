package com.paulvarry.intra42.ui;

import android.os.Bundle;
import android.support.design.widget.NavigationView;

import java.io.IOException;

public abstract class BasicThreadActivity extends BasicActivity implements NavigationView.OnNavigationItemSelectedListener {

    private GetDataOnMain getDataOnMain;
    private GetDataOnThread getDataOnTread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void refresh() {
        ThreadStatusCode statusDataOnMain = null;
        if (getDataOnMain != null) {
            statusDataOnMain = getDataOnMain.getDataOnMainThread();

            if (statusDataOnMain == ThreadStatusCode.FINISH)
                setViewState(StatusCode.CONTENT);
        }
        if (getDataOnTread != null && statusDataOnMain != ThreadStatusCode.FINISH) {
            setViewState(StatusCode.LOADING);
            new Thread(new Runnable() {
                @Override
                public void run() {

                    try {
                        getDataOnTread.getDataOnOtherThread();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                setViewState(StatusCode.CONTENT);
                            }
                        });

                    } catch (IOException e) {
                        e.printStackTrace();
                        setViewStateThread(StatusCode.NETWORK_ERROR);
                    } catch (UnauthorizedException e) {
                        e.printStackTrace();
                        setViewStateThread(StatusCode.API_UNAUTHORIZED);
                    } catch (ErrorServerException e) {
                        e.printStackTrace();
                        setViewStateThread(StatusCode.API_DATA_ERROR);
                    } catch (RuntimeException e) {
                        e.printStackTrace();
                        setViewStateThread(StatusCode.API_DATA_ERROR);
                    }
                }
            }).start();
        }
        if (getDataOnMain == null && getDataOnTread == null)
            setViewState(StatusCode.CONTENT);
    }

    public void registerGetDataOnMainTread(GetDataOnMain getDataOnMain) {
        this.getDataOnMain = getDataOnMain;
    }

    public void registerGetDataOnOtherThread(GetDataOnThread getDataOnTread) {
        this.getDataOnTread = getDataOnTread;
    }

    protected enum ThreadStatusCode {
        /**
         * When getting data is finish.
         */
        FINISH,
        /**
         * When need to get more data (on the otherThread).
         */
        CONTINUE,
        NONE
    }

    public interface GetDataOnMain {
        /**
         * Triggered when the activity start.
         * <p>
         * This method is run on main Thread, so you can make api call.
         *
         * @return Return ThreadStatusCode of what appending {@link GetDataOnThread#getDataOnOtherThread()}.
         */
        ThreadStatusCode getDataOnMainThread();
    }

    public interface GetDataOnThread {
        /**
         * Triggered when the activity start.
         * <p>
         * This method is run on main Thread, so you can make api call.
         *
         * @return Return ThreadStatusCode of what appending {@link BasicThreadActivity.GetDataOnMain#getDataOnMainThread()}.
         */
        void getDataOnOtherThread() throws IOException, RuntimeException;
    }

    public static class UnauthorizedException extends RuntimeException {
    }

    public static class ErrorServerException extends RuntimeException {
    }
}
