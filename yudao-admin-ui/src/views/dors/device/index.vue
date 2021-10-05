<template>
  <div class="app-container">

    <!-- 搜索工作栏 -->
    <el-form :model="queryParams" ref="queryForm" :inline="true" v-show="showSearch" label-width="68px">
      <!-- <el-form-item label="所属房间" prop="room">
        <el-input v-model="queryParams.room" placeholder="请输入所属房间" clearable size="small" @keyup.enter.native="handleQuery"/>
      </el-form-item> -->
      <el-form-item label="设备类型" prop="type">
        <el-select v-model="queryParams.type" placeholder="请选择设备类型" clearable size="small">
          <el-option v-for="dict in this.getDictDatas(DICT_TYPE.DORS_DEVICE_TYPE)"
                       :key="dict.value" :label="dict.label" :value="dict.value"/>
        </el-select>
      </el-form-item>
      <!-- <el-form-item label="设备序列号" prop="serialNo">
        <el-input v-model="queryParams.serialNo" placeholder="请输入设备序列号" clearable size="small" @keyup.enter.native="handleQuery"/>
      </el-form-item> -->
      <el-form-item label="MAC" prop="deviceMac">
        <el-input v-model="queryParams.deviceMac" placeholder="请输入设备MAC地址" clearable size="small" @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <!-- <el-form-item label="应用程序版本" prop="appVersion">
        <el-input v-model="queryParams.appVersion" placeholder="请输入应用程序版本" clearable size="small" @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="SDK版本" prop="sdkVersion">
        <el-input v-model="queryParams.sdkVersion" placeholder="请输入SDK版本" clearable size="small" @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="系统版本" prop="sysVersion">
        <el-input v-model="queryParams.sysVersion" placeholder="请输入系统版本" clearable size="small" @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="最近一次上线IP地址" prop="lastOnlineIp">
        <el-input v-model="queryParams.lastOnlineIp" placeholder="请输入最近一次上线IP地址" clearable size="small" @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="最近一次上线时间">
        <el-date-picker v-model="dateRangeLastOnlineTime" size="small" style="width: 240px" value-format="yyyy-MM-dd"
                        type="daterange" range-separator="-" start-placeholder="开始日期" end-placeholder="结束日期" />
      </el-form-item>
      <el-form-item label="创建时间">
        <el-date-picker v-model="dateRangeCreateTime" size="small" style="width: 240px" value-format="yyyy-MM-dd"
                        type="daterange" range-separator="-" start-placeholder="开始日期" end-placeholder="结束日期" />
      </el-form-item>
      <el-form-item label="备注" prop="remarks">
        <el-input v-model="queryParams.remarks" placeholder="请输入备注" clearable size="small" @keyup.enter.native="handleQuery"/>
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
                   v-hasPermi="['dors:device:create']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport"
                   v-hasPermi="['dors:device:export']">导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <!-- 列表 -->
    <el-table v-loading="loading" :data="list">
      <!-- <el-table-column label="主键（自增）" align="center" prop="id" /> -->
      <el-table-column label="所属房间" align="center" prop="room">
        <template slot-scope="scope">
          <span>{{ getRoomName(scope.row.room) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="设备类型" align="center" prop="type">
        <template slot-scope="scope">
          <span>{{ getDictDataLabel(DICT_TYPE.DORS_DEVICE_TYPE, scope.row.type) }}</span>
        </template>
      </el-table-column>
      <!-- <el-table-column label="设备序列号" align="center" prop="serialNo" /> -->
      <el-table-column label="设备MAC" align="center" prop="deviceMac" width="200" />
      <el-table-column label="应用版本" align="center" prop="appVersion" />
      <el-table-column label="SDK版本" align="center" prop="sdkVersion" />
      <el-table-column label="系统版本" align="center" prop="sysVersion" />
      <el-table-column label="IP" align="center" prop="lastOnlineIp" />
      <el-table-column label="最近上线时间" align="center" prop="lastOnlineTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.lastOnlineTime) }}</span>
        </template>
      </el-table-column>
      <!-- <el-table-column label="创建时间" align="center" prop="createTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column> -->
      <!-- <el-table-column label="备注" align="center" prop="remarks" /> -->
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)"
                     v-hasPermi="['dors:device:update']">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)"
                     v-hasPermi="['dors:device:delete']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <!-- 分页组件 -->
    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNo" :limit.sync="queryParams.pageSize"
                @pagination="getList"/>

    <!-- 对话框(添加 / 修改) -->
    <el-dialog :title="title" :visible.sync="open" width="600px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="150px">
        <el-form-item label="所属房间" prop="room">
          <!-- <el-input v-model="form.room" placeholder="请输入所属房间" /> -->
          <el-select v-model="form.room" placeholder="请输入所属房间">
            <el-option v-for="r in this.roomList"
                       :key="r.id" :label="r.name" :value="r.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="设备类型" prop="type">
          <el-select v-model="form.type" placeholder="请选择设备类型">
            <el-option v-for="dict in this.getDictDatas(DICT_TYPE.DORS_DEVICE_TYPE)"
                       :key="dict.value" :label="dict.label" :value="dict.value" />
          </el-select>
        </el-form-item>
        <!-- <el-form-item label="设备序列号" prop="serialNo">
          <el-input v-model="form.serialNo" placeholder="请输入设备序列号" />
        </el-form-item> -->
        <el-form-item label="设备MAC地址" prop="deviceMac">
          <el-input v-model="form.deviceMac" placeholder="请输入设备MAC地址" />
        </el-form-item>
        <el-form-item label="应用程序版本" prop="appVersion">
          <el-input v-model="form.appVersion" placeholder="请输入应用程序版本" />
        </el-form-item>
        <el-form-item label="SDK版本" prop="sdkVersion">
          <el-input v-model="form.sdkVersion" placeholder="请输入SDK版本" />
        </el-form-item>
        <el-form-item label="系统版本" prop="sysVersion">
          <el-input v-model="form.sysVersion" placeholder="请输入系统版本" />
        </el-form-item>
        <el-form-item label="最近一次上线IP地址" prop="lastOnlineIp">
          {{ form.lastOnlineIp }}
          <!-- <el-input v-model="form.lastOnlineIp" placeholder="请输入最近一次上线IP地址" /> -->
        </el-form-item>
        <el-form-item label="最近一次上线时间" prop="lastOnlineTime">
          {{ parseTime(form.lastOnlineTime) }}
          <!-- <el-date-picker clearable size="small" v-model="form.lastOnlineTime" type="date" value-format="yyyy-MM-dd" placeholder="选择最近一次上线时间" /> -->
        </el-form-item>
        <el-form-item label="备注" prop="remarks">
          <el-input v-model="form.remarks" placeholder="请输入备注" />
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
import { createDevice, updateDevice, deleteDevice, getDevice, getDevicePage, exportDeviceExcel } from "@/api/dors/device";
import { getRoomPage } from "@/api/dors/room";

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
      // 设备列表
      list: [],
      // 房间列表
      roomList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      dateRangeLastOnlineTime: [],
      dateRangeCreateTime: [],
      // 查询参数
      queryParams: {
        pageNo: 1,
        pageSize: 10,
        room: null,
        type: null,
        serialNo: null,
        deviceMac: null,
        appVersion: null,
        sdkVersion: null,
        sysVersion: null,
        lastOnlineIp: null,
        remarks: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        type: [{ required: true, message: "设备类型（PAD/ENCODER/DECODER）不能为空", trigger: "change" }],
        // lastOnlineTime: [{ required: true, message: "最近一次上线时间不能为空", trigger: "blur" }],
      }
    };
  },
  computed: {

  },
  created() {
    this.getRoomList();
    this.getList();
  },
  methods: {
    /** 查询列表 */
    getList() {
      this.loading = true;
      // 处理查询参数
      let params = {...this.queryParams};
      this.addBeginAndEndTime(params, this.dateRangeLastOnlineTime, 'lastOnlineTime');
      this.addBeginAndEndTime(params, this.dateRangeCreateTime, 'createTime');
      // 执行查询
      getDevicePage(params).then(response => {
        this.list = response.data.list;
        this.total = response.data.total;
        this.loading = false;
      });
    },
    getRoomList() {
      getRoomPage({pageSize:100}).then(response => {
        this.roomList = response.data.list;
      });
    },
    getRoomName(roomId) {
      for (const item of this.roomList) {
        if (item.id === roomId) {
          return item.name
        }
      }
      return '';
    },
    /** 取消按钮 */
    cancel() {
      this.open = false;
      this.reset();
    },
    /** 表单重置 */
    reset() {
      this.form = {
        id: undefined,
        room: undefined,
        type: undefined,
        serialNo: undefined,
        deviceMac: undefined,
        appVersion: undefined,
        sdkVersion: undefined,
        sysVersion: undefined,
        lastOnlineIp: undefined,
        lastOnlineTime: undefined,
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
      this.dateRangeLastOnlineTime = [];
      this.dateRangeCreateTime = [];
      this.resetForm("queryForm");
      this.handleQuery();
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加设备";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id;
      getDevice(id).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改设备";
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
          updateDevice(this.form).then(response => {
            this.msgSuccess("修改成功");
            this.open = false;
            this.getList();
          });
          return;
        }
        // 添加的提交
        createDevice(this.form).then(response => {
          this.msgSuccess("新增成功");
          this.open = false;
          this.getList();
        });
      });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const id = row.id;
      this.$confirm('是否确认删除设备编号为"' + id + '"的数据项?', "警告", {
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning"
        }).then(function() {
          return deleteDevice(id);
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
      this.addBeginAndEndTime(params, this.dateRangeLastOnlineTime, 'lastOnlineTime');
      this.addBeginAndEndTime(params, this.dateRangeCreateTime, 'createTime');
      // 执行导出
      this.$confirm('是否确认导出所有设备数据项?', "警告", {
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning"
        }).then(function() {
          return exportDeviceExcel(params);
        }).then(response => {
          this.downloadExcel(response, '设备.xls');
        })
    }
  }
};
</script>
