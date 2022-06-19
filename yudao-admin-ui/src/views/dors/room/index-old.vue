<template>
  <div class="app-container">

    <!-- 搜索工作栏 -->
    <el-form :model="queryParams" ref="queryForm" :inline="true" v-show="showSearch" label-width="68px">
      <!-- <el-form-item label="类型（手术室/会议室）" prop="type">
        <el-select v-model="queryParams.type" placeholder="请选择类型（手术室/会议室）" clearable size="small">
          <el-option v-for="dict in this.getDictDatas(DICT_TYPE.DORS_ROOM_TYPE)"
                       :key="dict.value" :label="dict.label" :value="dict.value"/>
        </el-select>
      </el-form-item> -->
      <el-form-item label="房间名称" prop="name">
        <el-input v-model="queryParams.name" placeholder="请输入房间名称" clearable size="small" @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <!-- <el-form-item label="创建时间">
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
                   v-hasPermi="['dors:room:create']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport"
                   v-hasPermi="['dors:room:export']">导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <!-- 列表 -->
    <el-table v-loading="loading" :data="list">
      <!-- <el-table-column label="房间编号" align="center" prop="id" /> -->
      <el-table-column label="房间类型" align="center" prop="type">
        <template slot-scope="scope">
          <span>{{ getDictDataLabel(DICT_TYPE.DORS_ROOM_TYPE, scope.row.type) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="房间名称" align="center" prop="name" />
      <el-table-column label="绑定设备" align="center" prop="devices" width="400">
        <template slot-scope="scope">
          <div v-for="d in scope.row.devices" :key="d.id">{{ getDeviceLabel(d) }}</div>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" align="center" prop="createTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="备注" align="center" prop="remarks" :show-overflow-tooltip="true" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="200">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-circle-check" @click="handleBindDevice(scope.row)"
                     v-hasPermi="['dors:room:update']">绑定设备</el-button>
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)"
                     v-hasPermi="['dors:room:update']">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)"
                     v-hasPermi="['dors:room:delete']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <!-- 分页组件 -->
    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNo" :limit.sync="queryParams.pageSize"
                @pagination="getList"/>

    <!-- 对话框(步骤添加) -->
    <el-dialog :title="title" :visible.sync="addOpen" width="500px" append-to-body>
      <el-steps :active="addStep" finish-status="success" simple >
        <el-step title="房间信息"></el-step>
        <el-step title="绑定设备"></el-step>
      </el-steps>
      <el-form v-if="addStep === 0" ref="addFormStep1" :model="form" :rules="rules" label-width="80px" style="margin-top: 20px">
        <el-form-item label="房间类型" prop="type">
          <el-select v-model="form.type" placeholder="请选择类型">
            <el-option v-for="dict in getDictDatas(DICT_TYPE.DORS_ROOM_TYPE)"
                      :key="dict.value" :label="dict.label" :value="dict.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="房间名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入房间名称" />
        </el-form-item>
        <el-form-item label="备注" prop="remarks">
          <el-input v-model="form.remarks" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <el-form v-if="addStep === 1" ref="addFormStep2" :model="form" :rules="rules" label-width="80px" style="margin-top: 20px">
        <el-form-item label="操控面板" prop="padId">
          <el-select v-model="form.padId" placeholder="请选择操控面板">
            <el-option v-for="pd in getPadList"
                      :key="pd.id" :label="getDeviceLabel(pd)" :value="pd.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="编码器" prop="encoderId">
          <el-select v-model="form.encoderId" placeholder="请选择编码器">
            <el-option v-for="ed in getEncoderList"
                      :key="ed.id" :label="getDeviceLabel(ed)" :value="ed.id" />
          </el-select>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button v-if="0 < addStep" @click="prevStep">上一步</el-button>
        <el-button type="primary" @click="nextStep">{{addStepTitle}}</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
    
    <!-- 对话框(绑定设备表单) -->
    <el-dialog :title="title" :visible.sync="bindDeviceOpen" width="500px" append-to-body>
      <el-form ref="bindDeviceForm" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="操控面板" prop="padId">
          <el-select v-model="form.padId" placeholder="请选择操控面板">
            <el-option v-for="pd in getPadList"
                      :key="pd.id" :label="getDeviceLabel(pd)" :value="pd.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="编码器" prop="encoderId">
          <el-select v-model="form.encoderId" placeholder="请选择操控面板">
            <el-option v-for="ed in getEncoderList"
                      :key="ed.id" :label="getDeviceLabel(ed)" :value="ed.id" />
          </el-select>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitBindDeviceForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>

    <!-- 对话框(修改房间信息表单) -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="房间类型" prop="type">
          <el-select v-model="form.type" placeholder="请选择类型">
            <el-option v-for="dict in getDictDatas(DICT_TYPE.DORS_ROOM_TYPE)"
                       :key="dict.value" :label="dict.label" :value="dict.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="房间名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入房间名称" />
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
import { createRoom, updateRoom, bindDevice, deleteRoom, getRoom, getRoomPage, exportRoomExcel } from "@/api/dors/room";
import { getDictDataLabel, DICT_TYPE } from '@/utils/dict';
import { getDevicePage } from "@/api/dors/device";

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
      // 房间列表
      list: [],
      // 设备列表
      deviceList: [],
      // 弹出层标题
      title: "",
      // 是否显示添加弹出层
      addOpen: false,
      addStep: 0,
      addStepTitle: '下一步',
      // 是否显示绑定设备弹出层
      bindDeviceOpen: false,
      // 是否显示编辑弹出层
      open: false,
      dateRangeCreateTime: [],
      // 查询参数
      queryParams: {
        pageNo: 1,
        pageSize: 10,
        type: null,
        name: null,
        remarks: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        type: [{ required: true, message: "房间类型不能为空", trigger: "change" }],
        name: [{ required: true, message: "房间名称不能为空", trigger: "blur" }],
        padId: [{ required: true, message: "操控面板不能为空", trigger: "change" }],
        encoderId: [{ required: true, message: "编码器不能为空", trigger: "change" }],
      }
    };
  },
  computed: {
    getPadList() {
      return this.deviceList.filter((device) => {
        return 'PAD' === device.type && null === device.room;
      });
    },
    getEncoderList() {
      return this.deviceList.filter((device) => {
        return 'PAD' !== device.type && null === device.room;
      });
    },
    getDecoderList() {
      return this.deviceList.filter((device) => {
        return 'ENCODER' === device.type;
      });
    },
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
      getRoomPage(params).then(response => {
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
    getDeviceLabel(device) {
      return getDictDataLabel(DICT_TYPE.DORS_DEVICE_TYPE, device.type) + " - " + device.deviceMac + " - " + device.lastOnlineIp;
    },
    /** 取消按钮 */
    cancel() {
      this.open = false;
      this.addOpen = false;
      this.bindDeviceOpen = false;
      this.reset();
    },
    /** 表单重置 */
    reset() {
      this.form = {
        id: undefined,
        type: undefined,
        name: undefined,
        remarks: undefined,
      };
      this.resetForm("form");
      this.resetForm("addFormStep1");
      this.resetForm("addFormStep2");
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
      this.addOpen = true;
      this.addStep = 0; // 重置添加步骤状态
      this.addStepTitle = '下一步';
      this.title = "添加房间";
    },
    /** 下一步 */
    nextStep() {
      if(0 < this.addStep) {
        // 完成步骤，提交
        this.$refs["addFormStep2"].validate(valid => {
          if (!valid) {
            return;
          }
          console.log("nextStep submit");
          // 添加的提交
          createRoom(this.form).then(response => {
            // 新增返回的ID，再绑定设备
            let roomId = response.data;
            this.form.roomId = roomId;
            bindDevice(this.form).then(response => {
              this.msgSuccess("新增成功");
              this.addOpen = false;
              this.getDeviceList();
              this.getList();
            });
          });
        });
      } else {
        this.$refs["addFormStep1"].validate(valid => {
          if (!valid) {
            return;
          }
          this.addStep ++;
          this.addStepTitle = '确 定';
        });
      }
    },
    /** 上一步 */
    prevStep() {
      this.resetForm("addFormStep1");
      this.resetForm("addFormStep2");
      this.addStep --;
      this.addStepTitle = '下一步';
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id;
      getRoom(id).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改房间";
      });
    },
    handleBindDevice(row) {
      this.reset();
      this.bindDeviceOpen = true;
      this.title = "绑定设备";
      this.resetForm("bindDeviceForm");
      this.form.roomId = row.id;
      this.form.type = row.type;
      this.form.name = row.name;
    },
    /** 绑定设备提交按钮 */
    submitBindDeviceForm() {
      this.$refs["bindDeviceForm"].validate(valid => {
        if (!valid) {
          return;
        }
        // 绑定设备提交
        console.log(this.form);
        bindDevice(this.form).then(response => {
          this.msgSuccess("新增成功");
          this.bindDeviceOpen = false;
          this.getDeviceList();
          this.getList();
        });
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
          updateRoom(this.form).then(response => {
            this.msgSuccess("修改成功");
            this.open = false;
            this.getList();
          });
          return;
        }
      });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const id = row.id;
      this.$confirm('是否确认删除房间编号为"' + id + '"的数据项?', "警告", {
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning"
        }).then(function() {
          return deleteRoom(id);
        }).then(() => {
          this.getDeviceList();
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
      this.$confirm('是否确认导出所有房间数据项?', "警告", {
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning"
        }).then(function() {
          return exportRoomExcel(params);
        }).then(response => {
          this.downloadExcel(response, '房间.xls');
        })
    }
  }
};
</script>
