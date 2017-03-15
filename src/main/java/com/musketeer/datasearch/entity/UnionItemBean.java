package com.musketeer.datasearch.entity;

import com.musketeer.baselibrary.bean.BaseEntity;

/**
 * Created by zhongxuqi on 16-5-12.
 */
public class UnionItemBean extends BaseEntity {
    protected TaskBean Task;
    protected int[] Eval;

    public UnionItemBean() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public TaskBean getTask() {
        return Task;
    }

    public void setTask(TaskBean task) {
        Task = task;
    }

    public int[] getEval() {
        return Eval;
    }

    public void setEval(int[] eval) {
        Eval = eval;
    }
}
