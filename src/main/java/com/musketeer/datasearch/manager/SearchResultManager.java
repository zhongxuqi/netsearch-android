package com.musketeer.datasearch.manager;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.musketeer.baselibrary.util.LogUtils;
import com.musketeer.datasearch.MainApplication;
import com.musketeer.datasearch.R;
import com.musketeer.datasearch.adapter.DirectionaryListAdapter;
import com.musketeer.datasearch.entity.SearchResultEntity;
import com.musketeer.datasearch.other.SearchResultComparator;
import com.musketeer.datasearch.view.BaseDialog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by zhongxuqi on 16-5-13.
 */
public class SearchResultManager {
    private static SearchResultManager instance = null;
    protected AlertDialog mDirListDialog;

    public static SearchResultManager getInstance() {
        if (instance == null) {
            instance = new SearchResultManager();
        }
        return instance;
    }

    private SearchResultManager() {

    }

    public List<SearchResultEntity> UpdateListInfo(List<SearchResultEntity> list) {
        List<SearchResultEntity> updatedList = new ArrayList<>();
        if (list != null) {
            for (SearchResultEntity item: list) {
                SearchResultEntity localItem = MainApplication.getInstance()
                        .getSearchResultDao().getDataByLink(item.getLink());
                if (localItem != null) {
                    updatedList.add(localItem);
                } else {
                    updatedList.add(item);
                }
            }
        }
        return updatedList;
    }

    public void recordSearchResult(Activity activity, View v, SearchResultEntity entity, boolean isRecord) {
        entity.setParentId(0);
        if (isRecord) {
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            View view = LayoutInflater.from(activity).inflate(R.layout.include_dialog_directionary_list, null);
            builder.setView(view);
            ListView listView = (ListView) view.findViewById(R.id.dir_list);

            //加载数据
            List<SearchResultEntity> list = MainApplication.getInstance()
                    .getSearchResultDao().getAllFolderData();
            if (list != null) {
                Collections.sort(list, new SearchResultComparator());
            }
            DirectionaryListAdapter adapter = new DirectionaryListAdapter(activity, list);

            //添加根目录
            SearchResultEntity rootDir = new SearchResultEntity();
            rootDir.setTitle("根目录");
            rootDir.setId(0);
            rootDir.setFolder(true);
            adapter.addItemByIndex(rootDir, 0);

            listView.setAdapter(adapter);
            listView.setTag(entity);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    // TODO Auto-generated method stub
                    SearchResultEntity item = (SearchResultEntity) parent.getTag();
                    item.setRecorded(true);
                    item.setParentId(((SearchResultEntity) parent.getAdapter().getItem(position)).getId());
                    MainApplication.getInstance().getSearchResultDao().insertOrUpdate(item);
                    if (item.getStatusView() != null) item.getStatusView().setSelected(true);
                    mDirListDialog.dismiss();
                }
            });
            mDirListDialog = builder.create();
            mDirListDialog.show();
        } else {
            entity.setRecorded(false);
            if (entity.getStatusView() != null) entity.getStatusView().setSelected(false);
            MainApplication.getInstance().getSearchResultDao().deleteDataByUrlIfUseless(entity.getLink());
        }
    }

    public interface OnFinishListener {
        void onClick();
    }
}
