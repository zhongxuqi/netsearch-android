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
 * Created by zhongxuqi on 16-5-13.
 */
public class UnionListAdapter extends BaseRecyclerAdapter<SearchResultEntity, UnionListViewHolder> {
    private OnRecordListener recordListener;

    public UnionListAdapter(Context context) {
        super(context);
    }

    public UnionListAdapter(Context context, List<SearchResultEntity> list) {
        super(context, list);
    }

    public void setOnRecordListener(OnRecordListener l) {
        recordListener =l;
    }

    @Override
    public UnionListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_search_result_list, null);
        return new UnionListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(UnionListViewHolder holder, int position) {
        mDataList.get(position).setClickListener(clickListener);
        mDataList.get(position).setRecordListener(recordListener);
        holder.bindView(mDataList.get(position));
    }

    public interface OnRecordListener {
        void onRecordClick(View v, SearchResultEntity entity,boolean isRecord);
    }
}
class UnionListViewHolder extends RecyclerView.ViewHolder {
    TextView mTitle;
    ImageView mRecordStatus;
    TextView mAbstract;

    public UnionListViewHolder(View itemView) {
        super(itemView);
        mTitle=(TextView) itemView.findViewById(R.id.title);
        mRecordStatus=(ImageView) itemView.findViewById(R.id.status_personal_record);
        mAbstract=(TextView) itemView.findViewById(R.id.summary);
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
        mAbstract.setText(item.getAbstract());
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