<template>
  <div class="app-container">

    <!-- 搜索工作栏 -->
    <el-form :model="queryParams" ref="queryForm" :inline="true" v-show="showSearch" label-width="68px">
      <!-- <el-form-item label="所属手术室" prop="room">
        <el-select v-model="queryParams.room" placeholder="请选择所属手术室" clearable size="small">
          <el-option label="请选择字典生成" value="" />
        </el-select>
      </el-form-item> -->
      <el-form-item label="手术名称" prop="title">
        <el-input v-model="queryParams.title" placeholder="请输入手术名称" clearable size="small" @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="医生" prop="doctor">
        <el-input v-model="queryParams.doctor" placeholder="请输入医生" clearable size="small" @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="患者" prop="patient">
        <el-input v-model="queryParams.patient" placeholder="请输入患者" clearable size="small" @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <!-- <el-form-item label="手术简介" prop="operationInfo">
        <el-input v-model="queryParams.operationInfo" placeholder="请输入手术简介" clearable size="small" @keyup.enter.native="handleQuery"/>
      </el-form-item> -->
      <!-- <el-form-item label="创建时间">
        <el-date-picker v-model="dateRangeCreateTime" size="small" style="width: 240px" value-format="yyyy-MM-dd"
                        type="daterange" range-separator="-" start-placeholder="开始日期" end-placeholder="结束日期" />
      </el-form-item> -->
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <!-- 操作工具栏 -->
    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd"
                   v-hasPermi="['dors:operation-video:create']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport"
                   v-hasPermi="['dors:operation-video:export']">导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <!-- 列表 -->
    <el-table v-loading="loading" :data="list">
      <!-- <el-table-column label="主键（自增）" align="center" prop="id" /> -->
      <el-table-column label="所属手术室" align="center" prop="room" />
      <el-table-column label="手术名称" align="center" prop="title" />
      <el-table-column label="医生" align="center" prop="doctor" />
      <el-table-column label="患者" align="center" prop="patient" />
      <el-table-column label="手术简介" align="center" prop="operationInfo" />
      <el-table-column label="创建时间" align="center" prop="createTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)"
                     v-hasPermi="['dors:operation-video:update']">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)"
                     v-hasPermi="['dors:operation-video:delete']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <!-- 分页组件 -->
    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNo" :limit.sync="queryParams.pageSize"
                @pagination="getList"/>

    <!-- 对话框(添加 / 修改) -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="手术室" prop="room">
          <el-select v-model="form.room" clearable placeholder="请选择所属手术室">
            <el-option v-for="r in this.operatingRooms"
                       :key="r.id" :label="r.name" :value="r.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="手术名称" prop="title">
          <el-input v-model="form.title" placeholder="请输入手术名称" />
        </el-form-item>
        <el-form-item label="医生" prop="doctor">
          <el-input v-model="form.doctor" placeholder="请输入医生" />
        </el-form-item>
        <el-form-item label="患者" prop="patient">
          <el-input v-model="form.patient" placeholder="请输入患者" />
        </el-form-item>
        <el-form-item label="手术简介" prop="operationInfo">
          <el-input v-model="form.operationInfo" placeholder="请输入手术简介" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { createOperationVideo, updateOperationVideo, deleteOperationVideo, getOperationVideo, getOperationVideoPage, exportOperationVideoExcel } from "@/api/dors/operationVideo";
import { createVideoFile, updateVideoFile, deleteVideoFile, getVideoFile, getVideoFilePage, exportVideoFileExcel } from "@/api/dors/videoFile";
import { listOperatingRoom } from "@/api/dors/room";

export default {
  name: "OperationVideo",
  components: {
  },
  data() {
    return {
      // 遮罩层
      loading: true,
      // 显示搜索条件
      showSearch: true,
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
      operatingRooms: [],
      // 表单参数
      form: {},
      // 表单校验
      rules: {
      }
    };
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
    /** 取消按钮 */
    cancel() {
      this.open = false;
      this.reset();
    },
    /** 表单重置 */
    reset() {
      this.form = {
        room: undefined,
        title: undefined,
        doctor: undefined,
        patient: undefined,
        operationInfo: undefined,
      };
      this.resetForm("form");
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNo = 1;
      this.getList();
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.dateRangeCreateTime = [];
      this.resetForm("queryForm");
      this.handleQuery();
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加手术视频";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id;
      getOperationVideo(id).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改手术视频";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (!valid) {
          return;
        }
        // 修改的提交
        if (this.form.id != null) {
          updateOperationVideo(this.form).then(response => {
            this.msgSuccess("修改成功");
            this.open = false;
            this.getList();
          });
          return;
        }
        // 添加的提交
        createOperationVideo(this.form).then(response => {
          this.msgSuccess("新增成功");
          this.open = false;
          this.getList();
        });
      });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const id = row.id;
      this.$confirm('是否确认删除手术视频编号为"' + id + '"的数据项?', "警告", {
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning"
        }).then(function() {
          return deleteOperationVideo(id);
        }).then(() => {
          this.getList();
          this.msgSuccess("删除成功");
        })
    },
    /** 导出按钮操作 */
    handleExport() {
      // 处理查询参数
      let params = {...this.queryParams};
      params.pageNo = undefined;
      params.pageSize = undefined;
      this.addBeginAndEndTime(params, this.dateRangeCreateTime, 'createTime');
      // 执行导出
      this.$confirm('是否确认导出所有手术视频数据项?', "警告", {
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning"
        }).then(function() {
          return exportOperationVideoExcel(params);
        }).then(response => {
          this.downloadExcel(response, '手术视频.xls');
        })
    }
  }
};
</script>
