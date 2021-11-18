<template>
  <div class="app-container">
    <el-row :gutter="20">
      <!--部门数据-->
      <el-col :span="4" :xs="24">
        <!-- <div class="head-container">
          <el-input v-model="deptName" placeholder="请输入部门名称" clearable size="small" prefix-icon="el-icon-search" style="margin-bottom: 20px"/>
        </div> -->
        <div class="head-container">
          <el-tree :data="operatingRooms" :props="defaultProps" :expand-on-click-node="false"
                   ref="tree" default-expand-all @node-click="handleNodeClick"/>
        </div>
      </el-col>
      <!--用户数据-->
      <el-col :span="20" :xs="24">
        <!-- <el-form :model="queryParams" ref="queryForm" :inline="true" v-show="showSearch" label-width="68px">
          <el-form-item label="用户名称" prop="username">
            <el-input v-model="queryParams.username" placeholder="请输入用户名称" clearable size="small" style="width: 240px"
                      @keyup.enter.native="handleQuery"/>
          </el-form-item>
          <el-form-item label="手机号码" prop="mobile">
            <el-input v-model="queryParams.mobile" placeholder="请输入手机号码" clearable size="small" style="width: 240px"
                      @keyup.enter.native="handleQuery"/>
          </el-form-item>
          <el-form-item label="状态" prop="status">
            <el-select v-model="queryParams.status" placeholder="用户状态" clearable size="small" style="width: 240px">
              <el-option v-for="dict in statusDictDatas" :key="parseInt(dict.value)" :label="dict.label" :value="parseInt(dict.value)"/>
            </el-select>
          </el-form-item>
          <el-form-item label="创建时间">
            <el-date-picker v-model="dateRange" size="small" style="width: 240px" value-format="yyyy-MM-dd" type="daterange"
              range-separator="-" start-placeholder="开始日期" end-placeholder="结束日期"></el-date-picker>
          </el-form-item>
          <el-form-item>
            <el-button type="cyan" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
            <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
          </el-form-item>
        </el-form> -->

        <!-- <el-card v-for="v in list" :key="v.id" shadow="always" class="program">
          <img src="https://shadow.elemecdn.com/app/element/hamburger.9cf7b091-55e9-11e9-a976-7f4d0b07eef6.png" class="image">
          <div style="padding: 14px;">
            <span>{{ v.title }} <time class="time">{{ parseTime(v.createTime) }}</time></span>
            <el-select v-model="value" placeholder="请选择">
              <el-option
                v-for="item in cities"
                :key="item.value"
                :label="item.label"
                :value="item.value">
                <span style="float: left">{{ item.label }}</span>
                <span style="float: right; color: #8492a6; font-size: 13px">{{ item.value }}</span>
              </el-option>
            </el-select>
          </div>
        </el-card> -->
        <el-row :gutter="20">
          <el-col :span="6" v-for="v in list" :key="v.id" shadow="always" >
            <el-card :body-style="{ padding: '0px' }">
              <img src="https://shadow.elemecdn.com/app/element/hamburger.9cf7b091-55e9-11e9-a976-7f4d0b07eef6.png" class="image">
              <div style="padding: 14px;">
                <span class="video-title">{{ v.title }}</span><span class="time">{{ parseTime(v.createTime) }}</span>
              </div>
              <!-- <el-popover
                placement="right"
                width="400"
                trigger="hover">
                <el-table :data="gridData">
                    <el-table-column width="150" property="date" label="日期"></el-table-column>
                    <el-table-column width="100" property="name" label="姓名"></el-table-column>
                    <el-table-column width="300" property="address" label="地址"></el-table-column>
                </el-table>
                <el-button slot="reference" style="width: 100%;">查看通道视频</el-button>
              </el-popover> -->
              <el-select v-model="value" placeholder="查看通道视频" style="width: 100%;">
                <el-option
                    v-for="item in cities"
                    :key="item.value"
                    :label="item.label"
                    :value="item.value">
                    <span style="float: left">{{ item.label }}</span>
                    <span style="float: right; color: #8492a6; font-size: 13px">{{ item.value }}</span>
                </el-option>
              </el-select>
            </el-card>
          </el-col>
        </el-row>
        <pagination v-show="total>0" :total="total" :page.sync="queryParams.pageNo" :limit.sync="queryParams.pageSize"
                    @pagination="getList"/>
      </el-col>
    </el-row>
    <!-- 对话框(添加 / 修改) -->
  </div>
</template>

<script>
import Treeselect from "@riophae/vue-treeselect";
import "@riophae/vue-treeselect/dist/vue-treeselect.css";
import { listOperatingRoom } from "@/api/dors/room";
import { createOperationVideo, updateOperationVideo, deleteOperationVideo, getOperationVideo, getOperationVideoPage, exportOperationVideoExcel } from "@/api/dors/operationVideo";


export default {
  name: "OperationVideoVod",
  components: { Treeselect },
  data() {
    return {
      // 遮罩层
      loading: true,
      // 显示搜索条件
      showSearch: true,
      // 手术室列表
      operatingRooms: [],
      defaultProps: {
        children: "children",
        label: "name"
      },
      // 总条数
      total: 0,
      // 手术视频列表
      list: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      dateRangeCreateTime: [],
      // 查询参数
      queryParams: {
        pageNo: 1,
        pageSize: 10,
        room: null,
        title: null,
        doctor: null,
        patient: null,
        operationInfo: null,
      },
    };
  },
  watch: {
    // 根据名称筛选部门树
    deptName(val) {
      this.$refs.tree.filter(val);
    }
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询列表 */
    getList() {
      this.loading = true;
      // 处理查询参数
      let params = {...this.queryParams};
      this.addBeginAndEndTime(params, this.dateRangeCreateTime, 'createTime');
      // 获取手术室列表
      listOperatingRoom().then(response => {
        this.operatingRooms = response.data;
      });
      // 执行查询
      getOperationVideoPage(params).then(response => {
        this.list = response.data.list;
        this.total = response.data.total;
        this.loading = false;
      });
    },
    // 节点单击事件
    handleNodeClick(data) {
      this.queryParams.room = data.id;
      this.getList();
    },
  }
};
</script>
<style>
  .el-dropdown-link {
    cursor: pointer;
    color: #1890ff;
    margin-left: 5px;
  }
  .el-icon-arrow-down {
    font-size: 14px;
  }
  .program {
      width: 24%;
  }
  .video-title {
  }
  .time {
    padding-left: 10px;
    font-size: 13px;
    color: #999;
  }
  .bottom {
    margin-top: 13px;
    line-height: 12px;
  }
  .button {
    padding: 0;
    float: right;
  }
  .image {
    width: 100%;
    display: block;
  }
</style>
