<template>
  <div class="app-container">

    <!-- 搜索工作栏 -->
    <el-form :model="queryParams" ref="queryForm" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="名称" prop="name">
        <el-input v-model="queryParams.name" placeholder="请输入名称" clearable size="small" @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <!-- <el-form-item label="包名" prop="packageName">
        <el-input v-model="queryParams.packageName" placeholder="请输入包名" clearable size="small" @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="版本号" prop="versionNum">
        <el-input v-model="queryParams.versionNum" placeholder="请输入版本号" clearable size="small" @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="版本代码" prop="versionCode">
        <el-input v-model="queryParams.versionCode" placeholder="请输入版本代码" clearable size="small" @keyup.enter.native="handleQuery"/>
      </el-form-item> -->
      <el-form-item label="创建时间">
        <el-date-picker v-model="dateRangeCreateTime" size="small" style="width: 240px" value-format="yyyy-MM-dd"
                        type="daterange" range-separator="-" start-placeholder="开始日期" end-placeholder="结束日期" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <!-- 操作工具栏 -->
    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd"
                   v-hasPermi="['dors:version:create']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport"
                   v-hasPermi="['dors:version:export']">导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <!-- 列表 -->
    <el-table v-loading="loading" :data="list">
      <!-- <el-table-column label="编号（自增）" align="center" prop="id" /> -->
      <el-table-column label="图标" align="center" prop="iconUrl">
        <template slot-scope="scope">
          <img v-if="'' !== scope.row.iconUrl && null !== scope.row.iconUrl" width="200px" :src="scope.row.iconUrl">
          <i v-else>无图</i>
        </template>
      </el-table-column>
      <el-table-column label="名称" align="center" prop="name" />
      <el-table-column label="包名" align="center" prop="packageName" />
      <!-- <el-table-column label="版本号" align="center" prop="versionNum" /> -->
      <el-table-column label="版本代码" align="center" prop="versionCode" />
      <!-- <el-table-column label="是否强制升级" align="center" prop="forceUpdate" /> -->
      <!-- <el-table-column label="主入口Activity" align="center" prop="mainActivity" /> -->
      <!-- <el-table-column label="开发者" align="center" prop="author" /> -->
      <!-- <el-table-column label="开发者联系方式" align="center" prop="authorContact" /> -->
      <el-table-column label="描述" align="center" prop="description" />
      <!-- <el-table-column label="浏览次数" align="center" prop="hits" /> -->
      <!-- <el-table-column label="下载次数" align="center" prop="dlCount" /> -->
      <el-table-column label="更新时间" align="center" prop="updateTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.updateTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-link v-hasPermi="['dors:version:query']" target="_blank" 
            :href="scope.row.url" 
            :underline="false"
            style="color:#409EFF;font-size:12px;font-weight:400;"
          >
            <i class="el-icon-download"/>下载
            <!-- <el-button size="mini" icon="el-icon-download" style="border: none;" >下载</el-button> -->
          </el-link>
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)"
                     v-hasPermi="['dors:version:update']" style="margin-left: 10px;">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)"
                     v-hasPermi="['dors:version:delete']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <!-- 分页组件 -->
    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNo" :limit.sync="queryParams.pageSize"
                @pagination="getList"/>

    <!-- 对话框(添加 / 修改) -->
    <el-dialog :title="title" :visible.sync="open" @close="cancel" width="600px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入名称" />
        </el-form-item>
        <el-form-item label="包名" prop="packageName">
          <el-input v-model="form.packageName" placeholder="请输入包名" />
        </el-form-item>
        <el-form-item label="版本号" prop="versionNum">
          <el-input v-model="form.versionNum" placeholder="请输入版本号" />
        </el-form-item>
        <el-form-item label="版本代码" prop="versionCode">
          <el-input v-model="form.versionCode" placeholder="请输入版本代码" />
        </el-form-item>
        <el-form-item label="强制升级" prop="forceUpdate">
          <el-checkbox v-model="form.forceUpdate" />
        </el-form-item>
        <el-form-item label="文件">
          <el-upload
            ref="upload"
            :limit="1" 
            accept=".apk"
            class="upload-demo"
            drag
            :action="upload.url" 
            :headers="upload.headers"
            :data="upload.data" 
            :disabled="upload.isUploading"
            :on-change="handleFileChange"
            :on-progress="handleFileUploadProgress"
            :on-success="handleFileSuccess"
          >
            <i class="el-icon-upload"></i>
            <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
            <div class="el-upload__tip" slot="tip">只能上传apk文件，且不超过100MB</div>
          </el-upload>
        </el-form-item>
        <!-- <el-form-item label="主入口Activity" prop="mainActivity">
          <el-input v-model="form.mainActivity" placeholder="请输入主入口Activity" />
        </el-form-item>
        <el-form-item label="下载地址" prop="url">
          <el-input v-model="form.url" placeholder="请输入下载地址" />
        </el-form-item>
        <el-form-item label="校验码" prop="checksum">
          <el-input v-model="form.checksum" placeholder="请输入校验码" />
        </el-form-item>
        <el-form-item label="大小" prop="size">
          <el-input v-model="form.size" placeholder="请输入大小" />
        </el-form-item>
        <el-form-item label="开发者" prop="author">
          <el-input v-model="form.author" placeholder="请输入开发者" />
        </el-form-item>
        <el-form-item label="开发者联系方式" prop="authorContact">
          <el-input v-model="form.authorContact" placeholder="请输入开发者联系方式" />
        </el-form-item> -->
        <el-form-item label="更新日志" prop="changeLog">
          <el-input :rows="3" type="textarea" v-model="form.changeLog" placeholder="请输入更新日志" />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input :rows="3" type="textarea" v-model="form.description" placeholder="请输入描述" />
        </el-form-item>
        <!-- <el-form-item label="浏览次数" prop="hits">
          <el-input v-model="form.hits" placeholder="请输入浏览次数" />
        </el-form-item>
        <el-form-item label="下载次数" prop="dlCount">
          <el-input v-model="form.dlCount" placeholder="请输入下载次数" />
        </el-form-item>
        <el-form-item label="备注" prop="remarks">
          <el-input v-model="form.remarks" placeholder="请输入备注" />
        </el-form-item> -->
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { createVersion, updateVersion, deleteVersion, getVersion, getVersionPage, exportVersionExcel } from "@/api/dors/version";
import {getToken} from "@/utils/auth";

export default {
  name: "",
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
      // 应用版本列表
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
        name: null,
        packageName: null,
        versionNum: null,
        versionCode: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        versionNum: [{ required: true, message: "版本号不能为空", trigger: "blur" }],
      },
      // 用户导入参数
      upload: {
        open: false, // 是否显示弹出层
        title: "", // 弹出层标题
        isUploading: false, // 是否禁用上传
        url: process.env.VUE_APP_BASE_API + '/api/' + "/dors/version/upload", // 请求地址
        headers: { Authorization: "Bearer " + getToken() }, // 设置上传的请求头部
        data: {} // 上传的额外数据，用于文件名
      },
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
      // 执行查询
      getVersionPage(params).then(response => {
        this.list = response.data.list;
        this.total = response.data.total;
        this.loading = false;
      });
    },
    /** 取消按钮 */
    cancel() {
      this.$refs["upload"].clearFiles();
      this.open = false;
      this.reset();
    },
    /** 表单重置 */
    reset() {
      this.form = {
        id: undefined,
        name: undefined,
        packageName: undefined,
        versionNum: undefined,
        versionCode: undefined,
        forceUpdate: false,
        mainActivity: undefined,
        url: undefined,
        iconUrl: undefined,
        checksum: undefined,
        size: undefined,
        author: undefined,
        authorContact: undefined,
        changeLog: undefined,
        description: undefined,
        hits: undefined,
        dlCount: undefined,
        remarks: undefined,
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
      this.title = "添加应用版本";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id;
      getVersion(id).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改应用版本";
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
          updateVersion(this.form).then(response => {
            this.msgSuccess("修改成功");
            this.$refs["upload"].clearFiles();
            this.open = false;
            this.getList();
          });
          return;
        }
        // 添加的提交
        createVersion(this.form).then(response => {
          this.msgSuccess("新增成功");
          this.$refs["upload"].clearFiles();
          this.open = false;
          this.getList();
        });
      });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const id = row.id;
      this.$confirm('是否确认删除应用版本编号为"' + id + '"的数据项?', "警告", {
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning"
        }).then(function() {
          return deleteVersion(id);
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
      this.$confirm('是否确认导出所有应用版本数据项?', "警告", {
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning"
        }).then(function() {
          return exportVersionExcel(params);
        }).then(response => {
          this.downloadExcel(response, '应用版本.xls');
        })
    },
    /** 处理上传的文件发生变化 */
    handleFileChange(file, fileList) {
      console.log("handleFileChange", file, fileList);
    },
    /** 处理文件上传中 */
    handleFileUploadProgress(event, file, fileList) {
      this.upload.isUploading = true; // 禁止修改
    },
    /** 上传文件成功，将获取到的apk信息填充到表单中 */
    handleFileSuccess(res) {
      console.log("handleFileSuccess", res);
      this.form.name        = this.form.name        || res.data.name;
      this.form.packageName = this.form.packageName || res.data.packageName;
      this.form.versionNum  = this.form.versionNum  || res.data.versionNum;
      this.form.versionCode = this.form.versionCode || res.data.versionCode;
      this.form.url         = this.form.url         || res.data.url;
      this.form.iconUrl     = this.form.iconUrl     || res.data.iconUrl;
      this.form.checksum    = this.form.checksum    || res.data.checksum;
      this.form.size        = this.form.size        || res.data.size;

      this.upload.isUploading = false; // 禁止修改
      // 提示成功，并刷新
      // this.msgSuccess("上传成功");
    }
  }
};
</script>
