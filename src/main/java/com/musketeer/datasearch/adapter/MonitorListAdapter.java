package com.musketeer.datasearch.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.musketeer.baselibrary.adapter.BaseRecyclerAdapter;
import com.musketeer.datasearch.R;
import com.musketeer.datasearch.entity.SearchResultEntity;

import java.util.List;

/**
 * Created by zhongxuqi on 16-5-15.
 */
public class MonitorListAdapter extends BaseRecyclerAdapter<SearchResultEntity, MonitorListViewHolder> {
    private UnionListAdapter.OnRecordListener recordListener;

    public MonitorListAdapter(Context context) {
        super(context);
    }

    public MonitorListAdapter(Context context, List<SearchResultEntity> list) {
        super(context, list);
    }

    public UnionListAdapter.OnRecordListener getRecordListener() {
        return recordListener;
    }

    public void setRecordListener(UnionListAdapter.OnRecordListener recordListener) {
        this.recordListener = recordListener;
    }

    @Override
    public MonitorListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_simple_result_list, null);
        return new MonitorListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MonitorListViewHolder holder, int position) {
        mDataList.get(position).setClickListener(clickListener);
        mDataList.get(position).setRecordListener(recordListener);
        holder.bindView(mDataList.get(position));
    }
}
class MonitorListViewHolder extends RecyclerView.ViewHolder {
    TextView mTitle;
    ImageView mRecordStatus;

    public MonitorListViewHolder(View itemView) {
        super(itemView);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        itemView.setLayoutParams(lp);
        mTitle=(TextView) itemView.findViewById(R.id.title);
        mRecordStatus=(ImageView) itemView.findViewById(R.id.status_personal_record);
    }

    public void bindView(SearchResultEntity item) {
        itemView.setTag(item);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SearchResultEntity item = (SearchResultEntity) v.getTag();
                if (item.getClickListener() == null) return;
                item.getClickListener().onClick(item);
            }
        });
        mTitle.setText(item.getTitle());
        item.setStatusView(mRecordStatus);
        mRecordStatus.setSelected(item.isRecorded());
        mRecordStatus.setTag(item);
        mRecordStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                SearchResultEntity item = (SearchResultEntity) v.getTag();
                if (item.getRecordListener() == null) return;
                item.getRecordListener().onRecordClick(v, item, !v.isSelected());
            }
        });
        if (item.isRecorded()) {
            mRecordStatus.setSelected(true);
        } else {
            mRecordStatus.setSelected(false);
        }
    }
}