<template>
  <div class="app-container">
    <el-row :gutter="20">
      <el-col :span="6" v-for="v in list" :key="v.id" shadow="always" >
        <el-card :body-style="{ padding: '0px' }" class="live">
          <img :src="baseUrl + v.icon" class="image" @click="playVideo(v)">
          <div style="padding: 14px;">
            <span class="live-title">{{ v.name }}</span>
          </div>
          <!-- <div>
            <span class="time">{{ parseTime(v.createTime) }}</span>
          </div> -->
        </el-card>
      </el-col>
    </el-row>
    <pagination v-show="total>0" :total="total" :page.sync="queryParams.pageNo" :limit.sync="queryParams.pageSize" :pageSizes="queryPageSizes"
                @pagination="getList"/>
    <!-- 对话框(添加 / 修改) -->
    <el-dialog :title="title" :visible.sync="open" width="940px" append-to-body @close="playerClose">
      <video-player ref="player" :options="videoOptions"/>
    </el-dialog>
  </div>
</template>

<script>
import { getLivePage } from "@/api/dors/live";
import { concatBaseUrl } from '@/utils/ruoyi'
import VideoPlayer from "@/components/VideoPlayer";
import "video.js/dist/video-js.css"

export default {
  name: "LiveStream",
  components: { VideoPlayer },
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
      baseUrl: concatBaseUrl(''),
      selectedVideo: null,
      videoOptions: {
        autoplay: false,
        controls: true,
        width: '900px',
        //playbackRates: [0.5, 1, 1.5, 2],
        sources: []
      },
      // 总条数
      total: 0,
      // 手术视频列表
      list: [],
      prefix: '',
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      dateRangeCreateTime: [],
      player: null,
      // 查询参数
      queryParams: {
        pageNo: 1,
        pageSize: 12,
        room: null,
        title: null,
        doctor: null,
        patient: null,
        operationInfo: null,
        onlineStatus: true,
      },
      queryPageSizes: [12, 24, 36, 48],
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
        this.prefix = response.msg;
        this.loading = false;
      });
    },
    // 节点单击事件
    handleNodeClick(data) {
      this.queryParams.room = data.id;
      this.getList();
    },
    videoChange(val) {
      console.log("videoChange: ", val);
    },
    playVideo(video) {
      console.log("playVideo", video);
      if(video && null != video.addr) { // 选中了视频，并隐藏时，显示播放器开始播放。
        this.title = video.name;
        this.videoOptions.autoplay = true;
        let _currentUrl = video.addr;
        this.open = true;
        setTimeout(() => {
          console.log(this.$refs.player.player);
          this.$refs.player.player.src({
            src: _currentUrl,
            type: "application/x-mpegURL"
          });
          this.$refs.player.player.play();
        }, 100);
      }
    },
    playerClose() {
      this.$refs.player.player.pause();
      console.log("playerClose", this.$refs.player.player);
    },
  }
};
</script>
<style>
  .live {
    margin-bottom: 20px;;
  }
  .live-title {
  }
  .time {
    padding-left: 10px;
    font-size: 13px;
    color: #999;
  }
  .image {
    width: 100%;
    display: block;
  }
</style>
