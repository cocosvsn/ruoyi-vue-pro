<template>
  <div class="app-container">

    <!-- 搜索工作栏 -->
    <el-form :model="queryParams" ref="queryForm" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="房间名称" prop="name">
        <el-input v-model="queryParams.name" placeholder="请输入房间名称" clearable size="small" @keyup.enter.native="handleQuery"/>
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
                   v-hasPermi="['dors:room:create']">新增</el-button>
      </el-col>
      <!-- <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport"
                   v-hasPermi="['dors:room:export']">导出</el-button>
      </el-col> -->
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <!-- 列表 -->
    <el-table v-loading="loading" :data="list">
      <el-table-column type="expand" width="30">
        <template slot-scope="scopeRoom">
          <el-row :gutter="5">
            <el-col :span="2">
              {{ '操控面板 :' }}
            </el-col>
            <el-col :span="5">
              {{ 'MAC: ' + scopeRoom.row.pad.mac }}
            </el-col>
            <!-- <el-col :span="4">
              {{ '登陆密码: ' + (scopeRoom.row.pad.loginPass || "") }}
            </el-col> -->
          </el-row>
          <el-row v-for="(encoderDevice, encoderDeviceIndex) in scopeRoom.row.encoderDevices" :key="encoderDevice.id">
            <el-row :gutter="5">
              <el-col :span="2">
                {{ '编码器 ' + (encoderDeviceIndex + 1) + ' :' }}
              </el-col>
              <el-col :span="5">
                {{ encoderDevice.name }}
              </el-col>
            </el-row>
            <el-row :gutter="5">
              <el-col :span="2" :offset="1">
                编码器类型: 
              </el-col>
              <el-col :span="2">
                {{ getDictDataLabel(DICT_TYPE.DORS_ENCODER_TYPE, encoderDevice.manufacturer) }}
              </el-col>
              <el-col :span="2">
                编码器IP:
              </el-col>
              <el-col :span="2">
                {{ encoderDevice.ip }}
              </el-col>
            </el-row>
            <el-row v-for="(encoderChannel, encoderChannelIndex) in encoderDevice.channels" :key="encoderChannel.id" justify="flex" :gutter="5">
              <el-col :span="2" :offset="1">
                {{ '通道 ' + (encoderChannelIndex + 1) }}
              </el-col>
              <el-col :span="2">
                通道名称: 
              </el-col>
              <el-col :span="3">
                {{ encoderChannel.name }}
              </el-col>
              <el-col :span="2">
                矩阵端口: 
              </el-col>
              <el-col :span="2">
                &nbsp;{{ encoderChannel.matrixPort }}&nbsp;
              </el-col>
              <el-col :span="1">
                串口: 
              </el-col>
              <el-col :span="2">
                &nbsp;{{ encoderChannel.serialPort }}&nbsp;
              </el-col>
              <el-col :span="1">
                排序: 
              </el-col>
              <el-col :span="1">
                &nbsp;{{ encoderChannel.sort }}&nbsp;
              </el-col>
              <el-col :span="2">
                通道URL: 
              </el-col>
              <el-col :span="8">
                {{ encoderChannel.url }}
              </el-col>
            </el-row>
          </el-row>
          <el-row v-for="(decoderDevice, decoderDeviceIndex) in scopeRoom.row.decoderDevices" :key="decoderDevice.id">
            <el-row :gutter="5">
              <el-col :span="2">
                {{ '解码器 ' + (decoderDeviceIndex + 1) + ' :' }}
              </el-col>
              <el-col :span="5">
                {{ decoderDevice.name }}
              </el-col>
            </el-row>
            <el-row :gutter="5">
              <el-col :span="2" :offset="1">
                解码器类型: 
              </el-col>
              <el-col :span="2">
                {{ getDictDataLabel(DICT_TYPE.DORS_ENCODER_TYPE, decoderDevice.manufacturer) }}
              </el-col>
              <el-col :span="2">
                解码器IP:
              </el-col>
              <el-col :span="2">
                {{ decoderDevice.ip }}
              </el-col>
            </el-row>
            <el-row v-for="(decoderChannel, decoderChannelIndex) in decoderDevice.channels" :key="decoderChannel.id" justify="flex" :gutter="5">
              <el-col :span="2" :offset="1">
                {{ '通道 ' + (decoderChannelIndex + 1) }}
              </el-col>
              <el-col :span="2">
                拉流地址: 
              </el-col>
              <el-col :span="8">
                {{ decoderChannel.url }}
              </el-col>
            </el-row>
          </el-row>
          <el-row v-for="(ipcDevice, ipcDeviceIndex) in scopeRoom.row.ipcDevices" :key="ipcDevice.id">
            <el-row :gutter="5">
              <el-col :span="2">
                {{ 'IPC ' + (ipcDeviceIndex + 1) + ' :' }}
              </el-col>
              <el-col :span="5">
                {{ ipcDevice.name }}
              </el-col>
            </el-row>
            <el-row :gutter="5">
              <el-col :span="2" :offset="1">
                IPC类型: 
              </el-col>
              <el-col :span="2">
                {{ getDictDataLabel(DICT_TYPE.DORS_ENCODER_TYPE, ipcDevice.manufacturer) }}
              </el-col>
              <el-col :span="2">
                IPC IP:
              </el-col>
              <el-col :span="2">
                {{ ipcDevice.ip }}
              </el-col>
            </el-row>
            <el-row v-for="(ipcChannel, ipcChannelIndex) in ipcDevice.channels" :key="ipcChannel.id" justify="flex" :gutter="5">
              <el-col :span="2" :offset="1">
                {{ '通道 ' + (ipcChannelIndex + 1) }}
              </el-col>
              <el-col :span="2">
                通道名称: 
              </el-col>
              <el-col :span="3">
                {{ ipcChannel.name }}
              </el-col>
              <el-col :span="2">
                矩阵端口: 
              </el-col>
              <el-col :span="2">
                &nbsp;{{ ipcChannel.matrixPort }}&nbsp;
              </el-col>
              <el-col :span="1">
                串口: 
              </el-col>
              <el-col :span="2">
                &nbsp;{{ ipcChannel.serialPort }}&nbsp;
              </el-col>
              <el-col :span="1">
                排序: 
              </el-col>
              <el-col :span="1">
                &nbsp;{{ ipcChannel.sort }}&nbsp;
              </el-col>
              <el-col :span="2">
                通道URL: 
              </el-col>
              <el-col :span="8">
                {{ ipcChannel.url }}
              </el-col>
            </el-row>
          </el-row>
          <el-row v-for="(tvDevice, tvDeviceIndex) in scopeRoom.row.tvDevices" :key="tvDevice.id">
            <el-row :gutter="5">
              <el-col :span="2">
                {{ '大屏 ' + (tvDeviceIndex + 1) + ' :' }}
              </el-col>
            </el-row>
            <el-row :gutter="5">
              <el-col :span="2" :offset="1">
                大屏名称: 
              </el-col>
              <el-col :span="3">
                {{ tvDevice.name }}
              </el-col>
              <el-col :span="2">
                矩阵端口: 
              </el-col>
              <el-col :span="3">
                {{ tvDevice.ip }}
              </el-col>
            </el-row>
          </el-row>
        </template>
      </el-table-column>
      <el-table-column label="房间名称" align="center" prop="name" />
      <!-- <el-table-column label="编码器类型" align="center" prop="encoderType">
        <template slot-scope="scope">
          <span>{{ getDictDataLabel(DICT_TYPE.DORS_ENCODER_TYPE, scope.row.encoderType) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="编码器IP" align="center" prop="encoderIp" /> -->
      <el-table-column label="创建时间" align="center" prop="createTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="备注" align="center" prop="remarks" :show-overflow-tooltip="true" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="200">
        <template slot-scope="scope">
          <!-- <el-button size="mini" type="text" icon="el-icon-setting" @click="handleBatchConfigDecoderDevice(scope.row)"
                     v-hasPermi="['dors:device:update']">配置解码器</el-button> -->
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

    <!-- 对话框(修改房间信息表单) -->
    <el-dialog :title="title" :visible.sync="open" width="940px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="120px">
        <!-- <el-form-item label="房间类型" prop="type">
          <el-select v-model="form.type" placeholder="请选择类型">
            <el-option v-for="dict in getDictDatas(DICT_TYPE.DORS_ROOM_TYPE)"
                       :key="dict.value" :label="dict.label" :value="dict.value" />
          </el-select>
        </el-form-item> -->
        <el-form-item label="示教室名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入房间名称" />
        </el-form-item>
        <el-form-item label="操控面板" :required="true">
          <el-row :gutter="5">
            <!-- <el-col :span="3">
              <el-form-item prop="pad.name">
                <el-input v-model="pad.name" placeholder="操控面板名称" size="mini"/>
              </el-form-item>
            </el-col> -->
            <el-col :span="6">
              <el-form-item prop="pad.mac" :required="true" :rules="{
                  required: true, message: '操控面板MAC地址不能为空', trigger: 'blur'
                }"
              >
                <el-input v-model="form.pad.mac" placeholder="操控面板MAC地址" size="mini"/>
              </el-form-item>
            </el-col>
            <!-- <el-col :span="5">
              <el-form-item prop="pad.loginPass">
                <el-input v-model="form.pad.loginPass" placeholder="操控面板登陆密码" size="mini"/>
              </el-form-item>
            </el-col> -->
          </el-row>
        </el-form-item>
        <el-form-item label="全景摄像机">
          <el-button type="text" plain icon="el-icon-plus" size="mini" @click="handleAddDevice('IPC')">增加全景摄像机</el-button>
          <el-row v-for="(ipcDevice, ipcDeviceIndex) in form.ipcDevices" :key="ipcDevice.id">
            <el-row :gutter="5">
              <el-col :span="3">
                <el-form-item :prop="'ipcDevices.'+ipcDeviceIndex+'.name'">
                  <el-input :value="'全景摄像机 ' + (ipcDeviceIndex + 1)" readonly size="mini"/>
                </el-form-item>
              </el-col>
              <el-col :span="4">
                <el-form-item :prop="'ipcDevices.'+ipcDeviceIndex+'.name'"
                  :show-message="false"
                  :rules="{
                    required: true, message: '全景摄像机名称不能为空', trigger: 'change'
                  }"
                >
                  <el-input v-model="ipcDevice.name" placeholder="全景摄像机名称" size="mini"/>
                </el-form-item>
              </el-col>
              <el-col :span="6">
                <el-form-item :prop="'ipcDevices.'+ipcDeviceIndex+'.ip'"
                  :show-message="false"
                  :rules="{
                    required: true, message: '全景摄像机IP不能为空', trigger: 'blur'
                  }"
                >
                  <el-input v-model="ipcDevice.ip" :placeholder="'请输入全景摄像机 '+(ipcDeviceIndex + 1)+' IP'" size="mini"/>
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-button size="mini" type="text" icon="el-icon-delete" @click="handleRemoveDevice('全景摄像机', ipcDevice)" >删除全景摄像机</el-button>
                <el-button size="mini" type="text" icon="el-icon-plus" @click="handleAddDeviceChannel(ipcDevice)" >添加全景摄像机通道</el-button>
              </el-col>
            </el-row>
            <el-row v-for="(ipcChannel, ipcChannelIndex) in ipcDevice.channels" :key="ipcChannel.id">
              <el-row :gutter="5">
                <el-col :span="2" :offset="1">
                  {{'通道 ' + (ipcChannelIndex + 1)}}
                </el-col>
                <!-- <el-col :span="7">
                  <el-form-item :prop="'ipcDevices.'+ipcDeviceIndex+'.channels.'+ipcChannelIndex+'.name'"
                    :show-message="false"
                    :rules="{
                      required: true, message: '通道名称不能为空', trigger: 'change'
                    }"
                  >
                    <el-input v-model="ipcDevice.channels[ipcChannelIndex].name" placeholder="通道名称" clearable size="mini"/>
                  </el-form-item>
                </el-col> -->
                <el-col :span="14">
                  <el-form-item :prop="'ipcDevices.'+ipcDeviceIndex+'.channels.'+ipcChannelIndex+'.url'"
                    :show-message="false"
                    :rules="{
                      required: true, message: '通道URL不能为空', trigger: 'blur'
                    }"
                  >
                    <el-input v-model="ipcDevice.channels[ipcChannelIndex].url" placeholder="通道URL" clearable size="mini">
                      <!-- <template slot="prepend">rtsp://</template> -->
                    </el-input>
                  </el-form-item>
                </el-col>
              </el-row>
              <el-row :gutter="5">
                <el-col :span="4" :offset="3">
                  <el-form-item :prop="'ipcDevices.'+ipcChannelIndex+'.channels.'+ipcChannelIndex+'.sort'">
                    <el-input v-model="ipcDevice.channels[ipcChannelIndex].sort" placeholder="排序" clearable size="mini"/>
                  </el-form-item>
                </el-col>
                <el-col :span="4">
                  <el-form-item :prop="'ipcDevices.'+ipcDeviceIndex+'.channels.'+ipcChannelIndex+'.matrixPort'">
                    <el-input v-model="ipcDevice.channels[ipcChannelIndex].matrixPort" placeholder="矩阵端口" clearable size="mini"/>
                  </el-form-item>
                </el-col>
                <el-col :span="3">
                  <el-form-item :prop="'ipcDevices.'+ipcDeviceIndex+'.channels.'+ipcChannelIndex+'.serialPort'">
                    <el-input v-model="ipcDevice.channels[ipcChannelIndex].serialPort" placeholder="串口" clearable size="mini"/>
                  </el-form-item>
                </el-col>
                <el-col :span="8">
                  <el-button size="mini" type="text" icon="el-icon-delete" @click="handleRemoveDeviceChannel(ipcDevice, ipcChannel)" >删除</el-button>
                </el-col>
              </el-row>
            </el-row>
          </el-row>
        </el-form-item>
        <el-form-item label="编码通道">
          <el-button type="text" plain icon="el-icon-plus" size="mini" @click="handleAddDevice('ENCODER')">增加编码器</el-button>
          <el-row v-for="(encoderDevice, encoderDeviceIndex) in form.encoderDevices" :key="encoderDevice.id">
            <el-row :gutter="5">
              <el-col :span="3">
                <el-form-item :prop="'encoderDevices.'+encoderDeviceIndex+'.name'">
                  <el-input :value="'编码器 ' + (encoderDeviceIndex + 1)" readonly size="mini"/>
                </el-form-item>
              </el-col>
              <el-col :span="3">
                <el-form-item :prop="'encoderDevices.'+encoderDeviceIndex+'.name'"
                  :show-message="false"
                  :rules="{
                    required: true, message: '编码器名称不能为空', trigger: 'change'
                  }"
                >
                  <el-input v-model="encoderDevice.name" placeholder="编码器名称" size="mini"/>
                </el-form-item>
              </el-col>
              <el-col :span="4">
                <el-form-item :prop="'encoderDevices.'+encoderDeviceIndex+'.manufacturer'"
                  :show-message="false"
                  :rules="{
                    required: true, message: '编码器类型不能为空', trigger: 'change'
                  }"
                >
                  <el-select v-model="encoderDevice.manufacturer" :placeholder="'编码器 '+(encoderDeviceIndex + 1)+' 类型'" style="width: 100%;" size="mini" >
                    <el-option v-for="dict in getDictDatas(DICT_TYPE.DORS_ENCODER_TYPE)"
                              :key="dict.value" :label="dict.label" :value="dict.value" />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="4">
                <el-form-item :prop="'encoderDevices.'+encoderDeviceIndex+'.ip'"
                  :show-message="false"
                  :rules="{
                    required: true, message: '编码器IP不能为空', trigger: 'blur'
                  }"
                >
                  <el-input v-model="encoderDevice.ip" :placeholder="'编码器 '+(encoderDeviceIndex + 1)+' IP'" size="mini"/>
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-button size="mini" type="text" icon="el-icon-delete" @click="handleRemoveDevice('ENCODER', encoderDevice)" >删除编码器</el-button>
                <el-button size="mini" type="text" icon="el-icon-plus" @click="handleAddDeviceChannel(encoderDevice)" >添加编码器通道</el-button>
              </el-col>
            </el-row>
            <el-row v-for="(encoderChannel, encoderChannelIndex) in encoderDevice.channels" :key="encoderChannel.id">
              <el-row :gutter="5">
                <el-col :span="2" :offset="1">
                  {{'通道 ' + (encoderChannelIndex + 1)}}
                </el-col>
                <el-col :span="7">
                  <el-form-item :prop="'encoderDevices.'+encoderDeviceIndex+'.channels.'+encoderChannelIndex+'.name'"
                    :show-message="false"
                    :rules="{
                      required: true, message: '通道名称不能为空', trigger: 'change'
                    }"
                  >
                    <el-input v-model="encoderDevice.channels[encoderChannelIndex].name" placeholder="通道名称" clearable size="mini"/>
                  </el-form-item>
                </el-col>
                <el-col :span="14">
                  <el-form-item :prop="'encoderDevices.'+encoderDeviceIndex+'.channels.'+encoderChannelIndex+'.url'"
                    :show-message="false"
                    :rules="{
                      required: true, message: '通道URL不能为空', trigger: 'blur'
                    }"
                  >
                    <el-input v-model="encoderDevice.channels[encoderChannelIndex].url" placeholder="通道URL" clearable size="mini">
                      <!-- <template slot="prepend">rtsp://</template> -->
                    </el-input>
                  </el-form-item>
                </el-col>
              </el-row>
              <el-row :gutter="5">
                <el-col :span="4" :offset="3">
                  <el-form-item :prop="'encoderDevices.'+encoderDeviceIndex+'.channels.'+encoderChannelIndex+'.sort'">
                    <el-input v-model="encoderDevice.channels[encoderChannelIndex].sort" placeholder="排序" clearable size="mini"/>
                  </el-form-item>
                </el-col>
                <el-col :span="4">
                  <el-form-item :prop="'encoderDevices.'+encoderDeviceIndex+'.channels.'+encoderChannelIndex+'.matrixPort'">
                    <el-input v-model="encoderDevice.channels[encoderChannelIndex].matrixPort" placeholder="矩阵端口" clearable size="mini"/>
                  </el-form-item>
                </el-col>
                <el-col :span="3">
                  <el-form-item :prop="'encoderDevices.'+encoderDeviceIndex+'.channels.'+encoderChannelIndex+'.serialPort'">
                    <el-input v-model="encoderDevice.channels[encoderChannelIndex].serialPort" placeholder="串口" clearable size="mini"/>
                  </el-form-item>
                </el-col>
                <el-col :span="8">
                  <el-button size="mini" type="text" icon="el-icon-delete" @click="handleRemoveDeviceChannel(encoderDevice, encoderChannel)" >删除</el-button>
                </el-col>
              </el-row>
            </el-row>
          </el-row>
        </el-form-item>
        <el-form-item label="解码通道" :required="true">
          <el-button type="text" plain icon="el-icon-plus" size="mini" @click="handleAddDevice('DECODER')">增加解码器</el-button>
          <el-row v-for="(decoderDevice, decoderDeviceIndex) in form.decoderDevices" :key="decoderDevice.id">
            <el-row  :gutter="5">
              <el-col :span="3">
                <el-form-item>
                  <el-input :value="'解码器 ' + (decoderDeviceIndex + 1)" readonly size="mini"/>
                </el-form-item>
              </el-col>
              <el-col :span="3">
                <el-form-item :prop="'decoderDevices.'+decoderDeviceIndex+'.name'"
                  :show-message="false"
                  :rules="{
                    required: true, message: '解码器名称不能为空', trigger: 'change'
                  }"
                >
                  <el-input v-model="decoderDevice.name" placeholder="解码器名称" size="mini"/>
                </el-form-item>
              </el-col>
              <el-col :span="4">
                <el-form-item :prop="'decoderDevices.'+decoderDeviceIndex+'.manufacturer'"
                  :show-message="false"
                  :rules="{
                    required: true, message: '解码器类型不能为空', trigger: 'change'
                  }"
                >
                  <el-select v-model="decoderDevice.manufacturer" :placeholder="'解码器 '+(decoderDeviceIndex + 1)+' 类型'" style="width: 100%;" size="mini" >
                    <el-option v-for="dict in getDictDatas(DICT_TYPE.DORS_ENCODER_TYPE)"
                              :key="dict.value" :label="dict.label" :value="dict.value" />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="4">
                <el-form-item :prop="'decoderDevices.'+decoderDeviceIndex+'.ip'"
                  :show-message="false"
                  :rules="{
                    required: true, message: '解码器IP不能为空', trigger: 'blur'
                  }"
                >
                  <el-input v-model="decoderDevice.ip" :placeholder="'解码器 '+(decoderDeviceIndex + 1)+' IP'" size="mini"/>
                </el-form-item>
              </el-col>
              <el-col :span="4">
                <el-form-item :prop="'decoderDevices.'+decoderDeviceIndex+'.channelCount'"
                  :show-message="false"
                  :rules="{required: true, message: '解码器通道数量不能为空', trigger: 'change'}"
                >
                  <el-input-number v-model="decoderDevice.channelCount" 
                    @change="((currentValue, oldValue) => {handleDecoderDeviceChannelCountChange(decoderDevice, currentValue, oldValue)})" 
                    :min="0" :max="8" placeholder="通道数量" style="width: 100%;" size="mini" />
                </el-form-item>
              </el-col>
              <el-col :span="5">
                <el-button size="mini" type="text" icon="el-icon-delete" @click="handleRemoveDevice('DECODER', decoderDevice)" >删除解码器</el-button>
                <el-button size="mini" type="text" icon="el-icon-setting" @click="handleConfigDecoderDevice(decoderDevice)" v-hasPermi="['dors:device:update']">配置</el-button>
              </el-col>
            </el-row>
            <el-row v-for="(decoderChannel, decoderChannelIndex) in decoderDevice.channels" :key="decoderChannel.id" :gutter="5">
              <el-col :span="3" :offset="1">
                <el-form-item :prop="'decoderDevices.'+decoderDeviceIndex+'.channels.'+decoderChannelIndex+'.name'">
                  <el-input v-model="decoderDevice.channels[decoderChannelIndex].name" placeholder="通道名称" readonly size="mini"/>
                </el-form-item>
              </el-col>
              <el-col :span="20">
                <el-form-item :prop="'decoderDevices.'+decoderDeviceIndex+'.channels.'+decoderChannelIndex+'.url'">
                  <el-input v-model="decoderDevice.channels[decoderChannelIndex].url" readonly size="mini" >
                    <template slot="prepend">拉流地址：</template>
                    <el-button slot="append" icon="el-icon-copy-document" @click="handleCopyInputStream(decoderChannel)">复制拉流地址</el-button>
                  </el-input>
                </el-form-item>
              </el-col>
            </el-row>
          </el-row>
        </el-form-item>
        <el-form-item label="备注" prop="remarks">
          <el-input v-model="form.remarks" type="textarea" rows="3" placeholder="请输入备注" />
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
import { getDictDataLabel, DICT_TYPE, ROOM_TYPE, DEVICE_TYPE, SERVER_IP_CONFIG_KEY } from '@/utils/dict';
import { getConfigKey } from "@/api/infra/config";
import { configDecoderDevice } from "@/api/dors/device";

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
      // 服务器IP
      serverIp: undefined,
      // 是否显示添加弹出层
      addOpen: false,
      addStep: 0,
      addStepTitle: '下一步',
      // 是否显示绑定设备弹出层
      bindDeviceOpen: false,
      // 是否显示编辑弹出层
      open: false,
      dateRangeCreateTime: [],
      // 编码器数量
      encoderNum: 1,
      // 解码器数量
      decoderNum: 0,
      // IPC数量
      ipcNum: 0,
      // 添加设备时默认最多添加的设备数量
      defaultMaxDeviceCount: 4,
      // 添加设备时的默认值, 禁止赋值。初始化时使用 this.clone(this.defaultDevice) 进行拷贝使用。
      defaultDevice: {name: undefined, manufacturer: undefined, ip: undefined, channels: []},
      // 添加通道时的默认值, 禁止赋值。初始化时使用 this.clone(this.defaultChannel) 进行拷贝使用。
      defaultChannel: {name: undefined, url: undefined, sort: 0, matrixPort: undefined, serialPort: undefined},
      // 查询参数
      queryParams: {
        pageNo: 1,
        pageSize: 10,
        type: ROOM_TYPE.MEETING_ROOM,
        name: null,
        remarks: null,
      },
      // 表单参数
      form: {
        type: ROOM_TYPE.MEETING_ROOM,
        pad: {},            // 操控面板
        encoderDevices: [], // 编码器设备列表
        decoderDevices: [], // 解码器设备列表
        ipcDevices: [],     // IPC设备列表
        tvDevices: [],      // 显示屏设备列表
        remarks: undefined,
      },
      // 表单校验
      rules: {
        name: [{ required: true, message: "房间名称不能为空", trigger: "blur" }],
        decoderDevices: [{required: true, message: "请至少添加一个解码器", trigger: "change"}],
      }
    };
  },
  computed: {
    watchDecoderDeviceIp() { // 计算解码器IP
      return this.form.decoderDevices.map(d => {
        return d.ip;
      }).join();
    },
    watchIpcDeviceName() { // 计算IPC通道名称
      return this.form.ipcDevices.map(d => {
        return d.name;
      }).join();
    },
  },
  watch: {
    watchDecoderDeviceIp() { // 解码器IP发生变更
      this.form.decoderDevices.forEach(device => {
        device.channels.forEach((channel, channelIndex) => {
          channel.url = this.getDecoderChannelUrl('rtsp://', this.serverIp, device.ip, channelIndex);
        });
      });
    },
    watchIpcDeviceName() { // IPC设备名称发生变更
      this.form.ipcDevices.forEach(device => {
        device.channels.forEach((channel, channelIndex) => {
          channel.name = device.name; // + " " + (channelIndex + 1);
        });
      });
    },
  },
  created() {
    this.getList();
  },
  methods: {
    /** 
     * 解码器拉流地址生成 
     * 编码器拉流地址的配置规则，由于流都在服务器端转发。
     * 因此拉流地址的配置规则为： 
     * 协议://[配置的服务器地址[:配置的服务器端口]][/推流前缀][/解码器IP][/解码器通道索引]
     */
    getDecoderChannelUrl(protocol, serverIp, deviceIp, channelIndex) {
      return (protocol ? protocol : '') + 
          (serverIp ? serverIp : '') + 
          (deviceIp ? '/' + deviceIp.replaceAll('.', '-') : '') +
          '/' + (channelIndex ? channelIndex : '0')
    },
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
      getConfigKey(SERVER_IP_CONFIG_KEY).then(response => {
        this.serverIp = response.data;
      });
    },
    /** 取消按钮 */
    cancel() {
      this.open = false;
      this.reset();
    },
    /** 表单重置 */
    reset() {
      let tempDevice = this.clone(this.defaultDevice);
      let tempChannel = this.clone(this.defaultChannel);
      // 初始化时，默认有一个编码器，一个编码器通道。
      tempDevice.channels.push(tempChannel);
      this.form = {
        id: undefined,
        type: ROOM_TYPE.MEETING_ROOM,
        name: undefined,
        pad: this.clone(this.defaultDevice),
        encoderDevices: [],
        decoderDevices: [tempDevice],
        ipcDevices: [],
        tvDevices: [],
        maxMeetingNum: 4,
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
      this.title = "添加示教室";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id;
      getRoom(id).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改示教室";
      });
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
        console.log(this.form)
        // 修改的提交
        if (this.form.id != null) {
          updateRoom(this.form).then(response => {
            this.msgSuccess("修改成功");
            this.open = false;
            this.getList();
          });
          return;
        }
        // 添加的提交
        createRoom(this.form).then(response => {
          this.msgSuccess("新增成功");
          this.open = false;
          this.getList();
        });
      });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const id = row.id;
      this.$confirm('是否确认删除示教室："' + row.name + '" ?', "警告", {
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning"
        }).then(function() {
          return deleteRoom(id);
        }).then(() => {
          this.getList();
          this.msgSuccess("删除成功");
        })
    },
    /** 配置解码器设备的拉流地址 */
    handleConfigDecoderDevice(device) {
      console.log('handleConfigDecoderDevice', device);
      // IP地址校验正则表达式，支持IPv4和IPv6格式。
      let ipRegexp = /^((25[0-5]|2[0-4]\d|[01]?\d\d?)\.){3}(25[0-5]|2[0-4]\d|[01]?\d\d?)$|^([\da-fA-F]{1,4}:){6}((25[0-5]|2[0-4]\d|[01]?\d\d?)\.){3}(25[0-5]|2[0-4]\d|[01]?\d\d?)$|^::([\da-fA-F]{1,4}:){0,4}((25[0-5]|2[0-4]\d|[01]?\d\d?)\.){3}(25[0-5]|2[0-4]\d|[01]?\d\d?)$|^([\da-fA-F]{1,4}:):([\da-fA-F]{1,4}:){0,3}((25[0-5]|2[0-4]\d|[01]?\d\d?)\.){3}(25[0-5]|2[0-4]\d|[01]?\d\d?)$|^([\da-fA-F]{1,4}:){2}:([\da-fA-F]{1,4}:){0,2}((25[0-5]|2[0-4]\d|[01]?\d\d?)\.){3}(25[0-5]|2[0-4]\d|[01]?\d\d?)$|^([\da-fA-F]{1,4}:){3}:([\da-fA-F]{1,4}:){0,1}((25[0-5]|2[0-4]\d|[01]?\d\d?)\.){3}(25[0-5]|2[0-4]\d|[01]?\d\d?)$|^([\da-fA-F]{1,4}:){4}:((25[0-5]|2[0-4]\d|[01]?\d\d?)\.){3}(25[0-5]|2[0-4]\d|[01]?\d\d?)$|^([\da-fA-F]{1,4}:){7}[\da-fA-F]{1,4}$|^:((:[\da-fA-F]{1,4}){1,6}|:)$|^[\da-fA-F]{1,4}:((:[\da-fA-F]{1,4}){1,5}|:)$|^([\da-fA-F]{1,4}:){2}((:[\da-fA-F]{1,4}){1,4}|:)$|^([\da-fA-F]{1,4}:){3}((:[\da-fA-F]{1,4}){1,3}|:)$|^([\da-fA-F]{1,4}:){4}((:[\da-fA-F]{1,4}){1,2}|:)$|^([\da-fA-F]{1,4}:){5}:([\da-fA-F]{1,4})?$|^([\da-fA-F]{1,4}:){6}:$/i;
      if(!device || !device.ip || !ipRegexp.test(device.ip)) {
        this.msgWarning((device.name ? device.name : "")  + " 请先配置正确的解码器IP地址!");
        return;
      }

      if(!device.channelCount) {
        this.msgWarning((device.name ? device.name : "")  + " 解码器至少一个通道!");
        return;
      }

      if(!this.serverIp) {
        this.msgWarning("请使用【示教管理】【系统配置】中配置服务器IP地址!");
        return;
      }
      configDecoderDevice(device).then(rs => {
        let result = JSON.parse(rs.data);
        if(0 === rs.code && (result.result || "OK" == result.Result)) {
          this.msgSuccess(device.name + " 配置成功");
        } else {
          this.msgError(device.name + " 配置失败！");
        }
      }).catch(error => {
        console.error("解码器配置错误：", error);
        this.msgError("解码器【" + device.name + "】配置出错：" + error.message);
      });
    },
    /** 一键配置解码器设备的拉流地址 */
    handleBatchConfigDecoderDevice(row) {
      console.log("handleBatchConfigDecoderDevice", row.decoderDevices)
      row.decoderDevices.forEach(device => {
        this.handleConfigDecoderDevice(device);
      });
    },
    /** 根据设备类型添加设备 */
    handleAddDevice(deviceType) {
      let maxDeviceCount = this.defaultMaxDeviceCount; // 最大设备数量
      let tempDevice = this.clone(this.defaultDevice);
      let deviceList = undefined;
      let deviceTable = undefined;
      if(DEVICE_TYPE.ENCODER == deviceType) {
        deviceList = this.form.encoderDevices;
        // deviceTable = this.$refs.encoderDeviceTable;
        // 添加编码器时默认增加一个通道
        tempDevice.channels.push(this.clone(this.defaultChannel));
      } else if(DEVICE_TYPE.DECODER == deviceType) {
        deviceList = this.form.decoderDevices;
        // deviceTable = this.$refs.decoderDeviceTable;
      } else if(DEVICE_TYPE.IPC == deviceType) {
        deviceList = this.form.ipcDevices;
        // deviceTable = this.$refs.ipcDeviceTable;
        // 添加IPC时默认增加一个通道
        tempDevice.channels.push(this.clone(this.defaultChannel));
      } else if(DEVICE_TYPE.TV == deviceType) {
        deviceList = this.form.tvDevices;
        // deviceTable = this.$refs.tvDeviceTable;
      }

      if(maxDeviceCount > deviceList.length) { // 限制最多增加N个设备
        deviceList.push(tempDevice);
      } else {
        this.msgError("最多添加 " + maxDeviceCount + " 个设备！");
      }
    },
    /** 删除指定类型设备 */
    handleRemoveDevice(deviceType, item) {
      let deviceList = undefined;
      if(DEVICE_TYPE.ENCODER == deviceType) {
        deviceList = this.form.encoderDevices;
      } else if(DEVICE_TYPE.DECODER == deviceType) {
        deviceList = this.form.decoderDevices;
      } else if(DEVICE_TYPE.IPC == deviceType) {
        deviceList = this.form.ipcDevices;
      } else if(DEVICE_TYPE.TV == deviceType) {
        deviceList = this.form.tvDevices;
      }
      let index = deviceList.indexOf(item)
      if (index !== -1) {
        deviceList.splice(index, 1);
      }
    },
    /** 为设备添加通道 */
    handleAddDeviceChannel(item) {
      // console.log("handleAddDeviceChannel", item);
      if(8 > item.channels.length) { // 限制每个设备最多增加4个通道
        // item.channels.push(this.clone(this.defaultChannel));
        let tempChannel = this.clone(this.defaultChannel);
        tempChannel.name = "通道 " + (item.channels.length + 1)
        item.channels.push(tempChannel);
      } else {
        this.msgError("每个设备最多添加8个通道！");
      }
      // if(0 < item.channels.length) { // 如果当前通道数量大于0， 则默认展开行显示
      //   this.$refs.encoderDeviceTable.toggleRowExpansion(item, true);
      // }
    },
    /** 删除指定设备的某个通道 */
    handleRemoveDeviceChannel(device, channel) {
      let index = device.channels.indexOf(channel)
      if (index !== -1) {
        device.channels.splice(index, 1);
      }
      // if(0 >= device.channels.length) { // 如果当前通道数量小于等于0， 则默认收起行显示
      //   this.$refs.encoderDeviceTable.toggleRowExpansion(device, false);
      // }
    },
    /** 不再使用，表格形式表单用于展开行处理 */
    handleDeviceExpandChange(row, expandRows) {
      if(-1 !== expandRows.indexOf(row) && (!row.channels || 1 > row.channels.length)) { // 如果当前行不在展开列表中，且当前行没有通道，则不允许展开
        this.$refs.encoderDeviceTable.toggleRowExpansion(row, false);
      }
    },
    /** 解码器通道数量变更 */
    handleDecoderDeviceChannelCountChange(device, currentValue, oldValue) {
      // console.log("handleDecoderDeviceChannelCountChange", device);
      let tempChannels = [];
      if(currentValue) { // 增加通道数
        for(var i = 0; i < currentValue; i ++) {
          let tempChannel = {
            id: undefined, 
            name: '通道 ' + (i + 1), 
            url: this.getDecoderChannelUrl("rtsp://", this.serverIp, device.ip, i),
            sort: undefined
          };
          tempChannels.push(tempChannel);
        }
      }
      device.channels = tempChannels;
    },
    /** 复制网络输入流地址（用于编码器配置）*/ 
    handleCopyInputStream(channel) {
      // console.log("handleCopyInputStream", channel);
      // if(!this.form.id) {
      //   this.msgWarning("请先保存再拷贝");
      // }
      
      this.$copyText(channel.url).then(e => {
        // 拷贝成功
        this.msgSuccess(e.text + " 拷贝成功");
      }, err => {
        // 拷贝失败
        this.msgError(e.text + " 拷贝成功");
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
    },
    /** 对象复制，深拷贝 */
    clone(source,target) {
      var target = target || {}; // 最初的时候给它一个初始值=它自己或者是一个json
      for(var name in source){ // 遍历属性或元素列表
        if(typeof source[name] === "object"){ //先判断一下source[name]是不是一个对象
          target[name]= (source[name].constructor===Array)?[]:{}; //我们让要复制的对象的name项=数组或者是json
          this.clone(source[name],target[name]); //然后来无限调用函数自己 递归思想
        }else{
          target[name]=source[name];  //如果不是对象，直接等于即可，不会发生引用。
        }
      }
      return target; //然后在把复制好的对象给return出去
    }
  }
};
</script>
<style>
.channel-table {
  margin: 0 6px 0 30px;
}
.el-form-item__content .el-input-group {
  vertical-align: middle;
}
.el-input-group--append .el-input__inner, .el-input-group--prepend .el-input__inner {
  padding: 0 10px;
}
.operating-table-device-name {
  font-size: 16px;
}
.operating-table-expand {
  font-size: 12px;
  margin-left: 20px;
}
.operating-table-expand label {
  width: 90px;
  color: #99a9bf;
}
.operating-table-expand .el-form-item {
  margin-right: 0;
  margin-bottom: 0;
  width: 50%;
}
</style>