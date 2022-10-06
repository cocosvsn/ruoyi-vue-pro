<template>
  <div class="app-container">

    <!-- 搜索工作栏 -->
    <el-form :model="queryParams" ref="queryForm" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="标题" prop="name">
        <el-input v-model="queryParams.name" placeholder="请输入直播流标题" clearable size="small" @keydown.enter.native.stop="handleQuery" />
      </el-form-item>
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
                   v-hasPermi="['dors:live:create']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport"
                   v-hasPermi="['dors:live:export']">导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <!-- 列表 -->
    <el-table v-loading="loading" :data="list">
      <el-table-column label="封面图" align="center" prop="icon">
        <template slot-scope="scope">
          <img width="128px" height="128px" :src="baseUrl + scope.row.icon">
        </template>
      </el-table-column>
      <el-table-column label="标题" align="center" prop="name" />
      <el-table-column label="地址" align="center" prop="addr" />
      <el-table-column label="创建时间" align="center" prop="createTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)"
                     v-hasPermi="['dors:live:update']">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)"
                     v-hasPermi="['dors:live:delete']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <!-- 分页组件 -->
    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNo" :limit.sync="queryParams.pageSize"
                @pagination="getList"/>

    <!-- 对话框(添加 / 修改) -->
    <el-dialog :title="title" :visible.sync="open" width="600px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="标题" prop="name">
          <el-input v-model="form.name" placeholder="请输入直播流标题" />
        </el-form-item>
        <el-form-item label="地址" prop="addr">
          <el-input v-model="form.addr" placeholder="请输入地址" />
        </el-form-item>
        <el-form-item label="封面图" prop="icon">
          <!-- <el-input v-model="form.icon" placeholder="请输入封面图" /> -->
          <el-upload ref="upload" :limit="1" accept=".jpg, .png, .gif" :auto-upload="true" drag
                    class="icon-uploader"
                    :show-file-list="false"
                    :headers="upload.headers" :action="upload.url" :data="upload.data" :disabled="upload.isUploading"
                    :before-upload="handleBeforeUpload"
                    :on-change="handleFileChange"
                    :on-progress="handleFileUploadProgress"
                    :on-success="handleFileSuccess">
            <img v-if="upload.imageUrl" :src="upload.imageUrl" class="icon">
            <i v-else class="el-icon-plus avatar-uploader-icon"></i>
            <div class="el-upload__tip" style="color:red" slot="tip">提示：仅允许导入 jpg、png、gif 格式文件！建议尺寸: 512px * 512px 以内，小于60KB的图片。</div>
          </el-upload>
        </el-form-item>
        <el-form-item label="备注" prop="remarks">
          <el-input v-model="form.remarks" type="textarea" placeholder="请输入备注" />
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
import { createLive, updateLive, deleteLive, getLive, getLivePage, exportLiveExcel } from "@/api/dors/live";
import { concatBaseUrl } from '@/utils/ruoyi'
import { getToken } from "@/utils/auth";

export default {
  name: "Live",
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
      // 直播列表
      list: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      dateRangeCreateTime: [],
      baseUrl: concatBaseUrl(''),
      // 查询参数
      queryParams: {
        pageNo: 1,
        pageSize: 10,
        name: null,
      },
      // 图片上传参数
      upload: {
        isUploading: false,                                 // 是否禁用上传
        url: concatBaseUrl('/api/infra/file/upload'),       // 请求地址
        headers: { Authorization: "Bearer " + getToken() }, // 设置上传的请求头部
        imageUrl: null,                                     // 预览图片地址
        data: {}                                            // 上传的额外数据，用于文件名
      },
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
      // 执行查询
      getLivePage(params).then(response => {
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
        name: undefined,
        icon: undefined,
        addr: undefined,
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
      this.upload.imageUrl = null;
      this.open = true;
      this.title = "添加直播";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id;
      getLive(id).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改直播";
        this.upload.imageUrl = concatBaseUrl(this.form.icon);
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
          updateLive(this.form).then(response => {
            this.msgSuccess("修改成功");
            this.open = false;
            this.getList();
          });
          return;
        }
        // 添加的提交
        createLive(this.form).then(response => {
          this.msgSuccess("新增成功");
          this.open = false;
          this.getList();
        });
      });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const id = row.id;
      this.$confirm('是否确认删除直播编号为"' + id + '"的数据项?', "警告", {
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning"
        }).then(function() {
          return deleteLive(id);
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
      this.$confirm('是否确认导出所有直播数据项?', "警告", {
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning"
        }).then(function() {
          return exportLiveExcel(params);
        }).then(response => {
          this.downloadExcel(response, '直播.xls');
        })
    },
    /** 处理上传前的文件检测 */
    handleBeforeUpload(file) {
      console.log('handleBeforeUpload', file);
      const isJPG = file.type === 'image/jpeg' || file.type === 'image/png' || file.type === 'image/gif';
      const isLt60K = file.size / 1024  < 60;

      if (!isJPG) {
        this.$message.error('上传封面图只能是 JPG、PNG、GIF 格式!');
      }
      if (!isLt60K) {
        this.$message.error('上传封面图图片大小不能超过 60KB!');
      }
      return isJPG && isLt60K;
    },
    /** 处理上传的文件发生变化 */
    handleFileChange(file, fileList) {
      this.upload.data.path = file.name;
    },
    /** 处理文件上传中 */
    handleFileUploadProgress(event, file, fileList) {
      this.upload.isUploading = true; // 禁止修改
    },
    /** 文件上传成功处理 */
    handleFileSuccess(response, file, fileList) {
      this.upload.imageUrl = URL.createObjectURL(file.raw);
      this.form.icon = response.data;
    },
  }
};
</script>


<style>
.icon-uploader .el-upload {
  /* border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative; */
  
  overflow: hidden;
}
.icon-uploader .el-upload-dragger {
  width: 178px;
  height: 178px;
  line-height: 178px;
  text-align: center;
}
.icon-uploader .el-upload:hover {
  border-color: #409EFF;
}
.icon-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 178px;
  height: 178px;
  line-height: 178px;
  text-align: center;
}
.icon {
  width: 178px;
  height: 178px;
  display: block;
}
</style>