package com.musketeer.datasearch.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.musketeer.baselibrary.adapter.BaseRecyclerAdapter;
import com.musketeer.baselibrary.util.LogUtils;
import com.musketeer.datasearch.R;
import com.musketeer.datasearch.entity.MonitorResultResp;

import java.util.List;

/**
 * Created by zhongxuqi on 16-5-15.
 */
public class MonitorRespListAdapter extends BaseRecyclerAdapter<MonitorResultResp, MonitorRespListViewHolder> {

    protected OnRootClickListener rootClicklistener;

    public MonitorRespListAdapter(Context context) {
        super(context);
    }

    public MonitorRespListAdapter(Context context, List<MonitorResultResp> list) {
        super(context, list);
    }

    @Override
    public MonitorRespListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_monitor_search_list, null);
        return new MonitorRespListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MonitorRespListViewHolder holder, int position) {
        mDataList.get(position).setClickListener(clickListener);
        mDataList.get(position).setDeleteListener(deleteListener);
        mDataList.get(position).setRootClickListener(rootClicklistener);
        holder.bindView(mContext, mDataList.get(position));
    }

    public OnRootClickListener getRootClicklistener() {
        return rootClicklistener;
    }

    public void setRootClicklistener(OnRootClickListener rootClicklistener) {
        this.rootClicklistener = rootClicklistener;
    }

    public interface OnRootClickListener {
        void onRootClick(MonitorResultResp item);
    }
}
class MonitorRespListViewHolder extends RecyclerView.ViewHolder {
    TextView mTitle;
    ImageView mDetail;
    ImageView mDelete;
    TextView mTargetUrl;
    TextView mKeyword;

    public MonitorRespListViewHolder(View itemView) {
        super(itemView);
        mTitle = (TextView) itemView.findViewById(R.id.title);
        mDetail = (ImageView) itemView.findViewById(R.id.detail);
        mDelete = (ImageView) itemView.findViewById(R.id.delete);
        mTargetUrl = (TextView) itemView.findViewById(R.id.target_url);
        mKeyword = (TextView) itemView.findViewById(R.id.keyword);
    }

    public void bindView(Context context, MonitorResultResp item) {
        itemView.setTag(item);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MonitorResultResp item = (MonitorResultResp) v.getTag();
                if (item.getClickListener() == null) return;
                item.getClickListener().onClick(item);
            }
        });
        LogUtils.d("zxq", item.getTitle());
        mTitle.setText(item.getTitle());
        if (item.isNew()) {
            mTitle.setTextColor(context.getResources().getColor(R.color.app_theme_color));
        } else {
            mTitle.setTextColor(context.getResources().getColor(R.color.text_color));
        }
        mTargetUrl.setText(item.getUrl());
        mKeyword.setText(item.getKeyword());
        mDetail.setTag(item);
        mDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MonitorResultResp item = (MonitorResultResp) v.getTag();
                if (item.getRootClickListener() == null) return;
                item.getRootClickListener().onRootClick(item);
            }
        });
        mDelete.setTag(item);
        mDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MonitorResultResp item = (MonitorResultResp) v.getTag();
                if (item.getDeleteListener() == null) return;
                item.getDeleteListener().onItemDelete(item);
            }
        });
    }
}