<template>
  <div class="app-container">

    <!-- 搜索工作栏 -->
    <el-form :model="queryParams" ref="queryForm" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="所属设备" prop="device">
        <el-select v-model="queryParams.device" placeholder="请选择所属设备">
          <el-option v-for="ed in getEncoderList"
                    :key="ed.id" :label="getDeviceName(ed.id)" :value="ed.id" />
        </el-select>
        <!-- <el-input v-model="queryParams.device" placeholder="请输入所属设备" clearable size="small" @keyup.enter.native="handleQuery"/> -->
      </el-form-item>
      <el-form-item label="频道类型" prop="type">
        <el-select v-model="queryParams.type" placeholder="请选择频道类型" clearable size="small">
          <el-option v-for="dict in this.getDictDatas(DICT_TYPE.DORS_CHANNEL_TYPE)"
                       :key="dict.value" :label="dict.label" :value="dict.value"/>
        </el-select>
      </el-form-item>
      <el-form-item label="频道名称" prop="name">
        <el-input v-model="queryParams.name" placeholder="请输入频道名称" clearable size="small" @keyup.enter.native="handleQuery"/>
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
                   v-hasPermi="['dors:channel:create']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport"
                   v-hasPermi="['dors:channel:export']">导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <!-- 列表 -->
    <el-table v-loading="loading" :data="list">
      <!-- <el-table-column label="主键（自增）" align="center" prop="id" /> -->
      <el-table-column label="所属设备" align="center" prop="device" width="400">
        <template slot-scope="scope">
          <span>{{ getDeviceName(scope.row.device) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="频道类型" align="center" prop="type">
        <template slot-scope="scope">
          <span>{{ getDictDataLabel(DICT_TYPE.DORS_CHANNEL_TYPE, scope.row.type) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="频道名称" align="center" prop="name" />
      <!-- <el-table-column label="频道JSON数据信息" align="center" prop="jsonInfo" /> -->
      <el-table-column label="状态" align="center">
        <template slot-scope="scope">
          <el-switch v-model="scope.row.display" @change="handleStatusChange(scope.row)" />
        </template>
      </el-table-column>
      <el-table-column label="是否绑定摄像机" align="center">
        <template slot-scope="scope">
          <el-switch v-model="scope.row.isCamera" />
        </template>
      </el-table-column>
      <el-table-column label="修改时间" align="center" prop="updateTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.updateTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)"
                     v-hasPermi="['dors:channel:update']">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)"
                     v-hasPermi="['dors:channel:delete']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <!-- 分页组件 -->
    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNo" :limit.sync="queryParams.pageSize"
                @pagination="getList"/>

    <!-- 对话框(添加 / 修改) -->
    <el-dialog :title="title" :visible.sync="open" width="600px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="所属设备" prop="device">
          {{getDeviceName(form.device)}}
          <!-- <el-select v-model="form.device" placeholder="请选择所属设备">
            <el-option v-for="ed in deviceList"
                      :key="ed.id" :label="getDeviceName(ed.id)" :value="ed.id" />
          </el-select> -->
        </el-form-item>
        <el-form-item label="频道类型" prop="type">
          <el-select v-model="form.type" disabled placeholder="请选择频道类型">
            <el-option v-for="dict in this.getDictDatas(DICT_TYPE.DORS_CHANNEL_TYPE)"
                       :key="dict.value" :label="dict.label" :value="dict.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="频道名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入频道名称" />
        </el-form-item>
        <el-form-item label="状态" prop="display">
          <el-switch v-model="form.display" />
        </el-form-item>
        <el-form-item label="摄像机" prop="isCamera">
          <el-switch v-model="form.isCamera" />
        </el-form-item>
        <el-form-item v-if="form.isCamera" label="摄像机云台串口配置" prop="cameraSerialPort">
          <el-input type="textarea" rows="3" v-model="form.cameraSerialPort" placeholder="摄像机云台串口配置JSON" />
        </el-form-item>
        <el-form-item label="频道JSON数据信息" prop="jsonInfo">
          <el-input type="textarea" rows="3" v-model="form.jsonInfo" placeholder="请输入频道JSON数据信息" />
        </el-form-item>
        <el-form-item label="备注" prop="remarks">
          <el-input type="textarea" rows="3" v-model="form.remarks" placeholder="请输入备注" />
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
import { createChannel, updateChannel, deleteChannel, getChannel, getChannelPage, exportChannelExcel } from "@/api/dors/channel";
import { getDictDataLabel, DICT_TYPE } from '@/utils/dict';
import { SysCommonStatusEnum } from "@/utils/constants";
import { getDevicePage } from "@/api/dors/device";

export default {
  name: "",
  components: {
  },
  data() {
    const checkJson = (rule, value, callback) => {
      console.log(value);
      try {
        if(this.form.isCamera) {
          JSON.parse(value);
        }
        callback();
      } catch (e) {
        callback(new Error("JSON 格式错误，请仔细检查后重新输入！"));
      }
    };
    return {
      // 遮罩层
      loading: true,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 频道列表
      list: [],
      // 设备列表
      deviceList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      dateRangeCreateTime: [],
      // 查询参数
      queryParams: {
        pageNo: 1,
        pageSize: 10,
        device: null,
        type: 'mix',
        name: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        device: [{ required: true, message: "所属设备不能为空", trigger: "blur" }],
        type: [{ required: true, message: "频道类型（vi/usb/net/ndi/file/mix)不能为空", trigger: "change" }],
        name: [{ required: true, message: "频道名称不能为空", trigger: "blur" }],
        cameraSerialPort: [{required: true, validator: checkJson, trigger: "blur"}],
      }
    };
  },
  computed: {
    getEncoderList() {
      return this.deviceList.filter((device) => {
        return 'PAD' !== device.type;
      });
    }
  },
  created() {
    this.getDeviceList();
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
      getChannelPage(params).then(response => {
        this.list = response.data.list;
        this.total = response.data.total;
        this.loading = false;
      });
    },
    getDeviceList() {
      getDevicePage({pageSize:100}).then(response => {
        this.deviceList = response.data.list;
      });
    },
    getDeviceName(deviceId) {
      for (const item of this.deviceList) {
        if (item.id === deviceId) {
          return getDictDataLabel(DICT_TYPE.DORS_DEVICE_TYPE, item.type) + " - " + item.deviceMac + " - " + item.lastOnlineIp;
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
        device: undefined,
        type: undefined,
        name: undefined,
        display: undefined,
        isCamera: undefined,
        cameraSerialPort: undefined,
        jsonInfo: undefined,
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
      this.title = "添加频道";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id;
      getChannel(id).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改频道";
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
          updateChannel(this.form).then(response => {
            this.msgSuccess("修改成功");
            this.open = false;
            this.getList();
          });
          return;
        }
        // 添加的提交
        createChannel(this.form).then(response => {
          this.msgSuccess("新增成功");
          this.open = false;
          this.getList();
        });
      });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const id = row.id;
      this.$confirm('是否确认删除频道编号为"' + id + '"的数据项?', "警告", {
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning"
        }).then(function() {
          return deleteChannel(id);
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
      this.$confirm('是否确认导出所有频道数据项?', "警告", {
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning"
        }).then(function() {
          return exportChannelExcel(params);
        }).then(response => {
          this.downloadExcel(response, '频道.xls');
        })
    },
    // 修改频道显示状态
    handleStatusChange(row) {
      let text = row.display ? "启用" : "停用";
      this.$confirm('确认要"' + text + '""' + row.name + '"频道吗?', "警告", {
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning"
        }).then(function() {
          return updateChannel(row);
        }).then(() => {
          this.msgSuccess(text + "成功");
        }).catch(function() {
          row.display = !row.display;
        });
    }
  }
};
</script>
