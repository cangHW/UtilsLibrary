package com.perfect.library.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Canghaixiao.
 * Time : 2017/6/22 14:39.
 * Function :
 */
public class AlbumManager {

    private static final int FIND_ALL = -1;
    private static final int TYPE_FIND_ALL = 0x000;
    private static final int TYPE_FIND_NUM = 0x001;

    private Context mContext;
    private List<PictureMessage> mAllPictures = new ArrayList<>();

    public interface onFindToGroupFinishedListener {
        void onFinished(HashMap<String, List<PictureMessage>> hashMap);
    }

    public interface onFindFinishedListener {
        void onFinished(List<PictureMessage> list);
    }

    public static AlbumManager getInstence(Context context) {
        return new AlbumManager(context);
    }

    private AlbumManager(Context context) {
        this.mContext = context;
    }

    public void findFirstPicture(onFindFinishedListener findFinishedListener) {
        if (mAllPictures != null && mAllPictures.size() > 0) {
            List<PictureMessage> list = new ArrayList<>();
            list.add(mAllPictures.get(0));
            findFinishedListener.onFinished(list);
        } else {
            CheckPictureTask checkPictureTask = new CheckPictureTask(mAllPictures, TYPE_FIND_NUM);
            checkPictureTask.setFindFinishedListener(findFinishedListener);
            checkPictureTask.execute(1);
        }
    }

    public void findNumPicture(onFindFinishedListener findFinishedListener, int num) {
        if (mAllPictures != null && mAllPictures.size() > 0) {
            List<PictureMessage> list = new ArrayList<>();
            for (int i = 0; i < num; i++) {
                if (i < mAllPictures.size()) {
                    list.add(mAllPictures.get(i));
                }
            }
            findFinishedListener.onFinished(list);
        } else {
            CheckPictureTask checkPictureTask = new CheckPictureTask(mAllPictures, TYPE_FIND_NUM);
            checkPictureTask.setFindFinishedListener(findFinishedListener);
            checkPictureTask.execute(num);
        }
    }

    public void findAllPicture(onFindFinishedListener findFinishedListener) {
        if (mAllPictures != null && mAllPictures.size() > 0) {
            findFinishedListener.onFinished(mAllPictures);
        } else {
            CheckPictureTask checkPictureTask = new CheckPictureTask(mAllPictures, TYPE_FIND_ALL);
            checkPictureTask.setFindFinishedListener(findFinishedListener);
            checkPictureTask.execute(FIND_ALL);
        }
    }

    public void findAllPictureToGroup(onFindToGroupFinishedListener findAllFinishedListener) {
        if (mAllPictures != null && mAllPictures.size() > 0) {
            HashMap<String, List<PictureMessage>> hashMap = subGroupFromList(mAllPictures);
            findAllFinishedListener.onFinished(hashMap);
        } else {
            CheckPictureTask checkPictureTask = new CheckPictureTask(mAllPictures, TYPE_FIND_ALL);
            checkPictureTask.setFindToGroupFinishedListener(findAllFinishedListener);
            checkPictureTask.execute(FIND_ALL);
        }
    }

    private HashMap<String, List<PictureMessage>> subGroupFromList(List<PictureMessage> messageList) {
        if (messageList.size() == 0) {
            return null;
        }
        HashMap<String, List<PictureMessage>> hashMap = new HashMap<>();
        for (PictureMessage message : messageList) {
            if (!hashMap.containsKey(message.parentName)) {
                List<PictureMessage> chileList = new ArrayList<>();
                chileList.add(message);
                hashMap.put(message.parentName, chileList);
            } else {
                hashMap.get(message.parentName).add(message);
            }
        }
        return hashMap;
    }

    public class PictureMessage {
        //The album name
        public String parentName;
        //The picture url
        public String path;
        //The picture name
        public String pictureName;
        //The picture creation time
        public long createTime;
        //The desc of the picture
        public String desc;
    }

    private class CheckPictureTask extends AsyncTask<Integer, Integer, HashMap<String, List<PictureMessage>>> {

        private static final String MIME_type = MediaStore.Images.Media.MIME_TYPE;
        private static final String SORTORDER = MediaStore.Images.Media.DATE_MODIFIED;
        private static final String SELECTION = MIME_type + "=? or " + MIME_type + "=?";

        private List<PictureMessage> mMessageList;
        private int mFindType;
        private int mFindNum = FIND_ALL;
        private onFindToGroupFinishedListener mFindToGroupFinishedListener;
        private onFindFinishedListener mFindFinishedListener;

        CheckPictureTask(List<PictureMessage> messageList, int findType) {
            this.mMessageList = messageList;
            this.mFindType = findType;
        }

        void setFindToGroupFinishedListener(onFindToGroupFinishedListener findToGroupFinishedListener) {
            this.mFindToGroupFinishedListener = findToGroupFinishedListener;
        }

        void setFindFinishedListener(onFindFinishedListener findFinishedListener) {
            this.mFindFinishedListener = findFinishedListener;
        }

        @Override
        protected HashMap<String, List<PictureMessage>> doInBackground(Integer... params) {
            if (params != null && params.length > 0) {
                mFindNum = params[0];
            }
            HashMap<String, List<PictureMessage>> gruopMap = new HashMap<>();

            Uri imageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            String[] SELECTION_ARGS = new String[]{"image/jpeg", "image/png"};
            ContentResolver contentResolver = mContext.getContentResolver();
            Cursor cursor = null;
            try {
                cursor = contentResolver.query(imageUri, null, SELECTION, SELECTION_ARGS, SORTORDER);
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (cursor == null) {
                return null;
            }
            while (cursor.moveToNext()) {
                PictureMessage message = new PictureMessage();
                message.path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                message.parentName = new File(message.path).getParentFile().getName();
                message.pictureName = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME));
                message.createTime = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media.DATE_ADDED));
                message.desc = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DESCRIPTION));

                FileInputStream fs = null;
                try {
                    fs = new FileInputStream(new File(message.path));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                if (!gruopMap.containsKey(message.parentName)) {
                    List<PictureMessage> chileList = new ArrayList<>();
                    if (fs != null) {
                        mMessageList.add(0, message);
                        chileList.add(message);
                    }
                    gruopMap.put(message.parentName, chileList);
                } else {
                    if (fs != null) {
                        mMessageList.add(0, message);
                        gruopMap.get(message.parentName).add(message);
                    }
                }
                try {
                    if (fs != null) {
                        fs.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                ;
            }
            cursor.close();
            return gruopMap;
        }

        @Override
        protected void onPostExecute(HashMap<String, List<PictureMessage>> stringListMap) {
            super.onPostExecute(stringListMap);
            if (mFindToGroupFinishedListener != null) {
                mFindToGroupFinishedListener.onFinished(stringListMap);
            } else if (mFindFinishedListener != null) {
                if (mMessageList != null && mMessageList.size() > 0) {
                    if (mFindType == TYPE_FIND_ALL) {
                        mFindFinishedListener.onFinished(mMessageList);
                    } else {
                        if (mFindNum != -1) {
                            List<PictureMessage> list = new ArrayList<>();
                            for (int i = 0; i < mFindNum; i++) {
                                if (i < mMessageList.size()) {
                                    list.add(mMessageList.get(i));
                                }
                            }
                            mFindFinishedListener.onFinished(list);
                        } else {
                            mFindFinishedListener.onFinished(mMessageList);
                        }
                    }
                } else {
                    mFindFinishedListener.onFinished(null);
                }
            }
        }
    }
}
