// axios全局配置，后端启动地址
axios.defaults.baseURL = 'http://localhost:8080';
axios.defaults.headers.post['Content-Type'] = 'application/json';

new Vue({
  el: '#app',
  data: {
    // 登录态
    isLogin: false,
    isRegister: false,
    userId: null,
    // 登录注册表单
    form: {
      username: '',
      password: '',
      email: ''
    },
    // 导航标签
    tab: 'home',
    // 记账表单
    recordForm: {
      type: 'expense',
      amount: null,
      category: '餐饮',
      date: new Date().toISOString().split('T')[0],
      note: ''
    },
    // 流水列表
    recordList: [],
    // 统计数据
    totalIncome: 0,
    totalExpense: 0,
    recentRecords: [],
    // 预算相关
    budgetForm: {
      category: '全部分类',
      monthLimit: null,
      month: new Date().toISOString().slice(0,7)
    },
    budgetLimit: 0,
    categoryExpense: 0,
    // 图表实例
    pieChart: null,
    lineChart: null
  },
  // 页面加载时判断登录态
  mounted() {
    const userId = localStorage.getItem('userId');
    if (userId) {
      this.userId = userId;
      this.isLogin = true;
      this.initData();
    }
  },
  methods: {
    // 切换标签页
    changeTab(tabName) {
      this.tab = tabName;
      this.$nextTick(() => {
        if (tabName === 'statistic') {
          this.initCharts();
          this.getStatisticData();
        }
      });
    },
    // 注册
    async register() {
      if (!this.form.username || !this.form.password) {
        alert('请填写用户名和密码');
        return;
      }
      try {
        const res = await axios.post('/user/register', this.form);
        if (res.data.success) {
          alert(res.data.msg);
          this.isRegister = false;
          this.form.password = '';
        } else {
          alert(res.data.msg);
        }
      } catch (e) {
        alert('注册失败，请检查网络');
      }
    },
    // 登录
    async login() {
      if (!this.form.username || !this.form.password) {
        alert('请填写用户名和密码');
        return;
      }
      try {
        const res = await axios.post('/user/login', this.form);
        if (res.data.success) {
          alert(res.data.msg);
          this.isLogin = true;
          this.userId = res.data.userId;
          // 保存登录态
          localStorage.setItem('userId', this.userId);
          localStorage.setItem('username', res.data.username);
          // 初始化数据
          this.initData();
        } else {
          alert(res.data.msg);
        }
      } catch (e) {
        alert('登录失败，请检查后端服务是否启动');
      }
    },
    // 退出登录
    logout() {
      this.isLogin = false;
      this.userId = null;
      localStorage.clear();
      this.form = {username: '', password: '', email: ''};
    },
    // 初始化所有数据
    async initData() {
      await this.getRecordList();
      await this.calcTotal();
      await this.getBudget();
      if (this.tab === 'statistic') {
        this.initCharts();
        this.getStatisticData();
      }
    },
    // 获取流水列表
    async getRecordList() {
      try {
        const res = await axios.get('/transaction/list', {
          params: { userId: this.userId }
        });
        this.recordList = res.data.reverse(); // 最新的在前面
        this.recentRecords = this.recordList.slice(0, 5); // 最近5条
      } catch (e) {
        console.error('获取流水失败', e);
      }
    },
    // 新增流水记录
    async addRecord() {
      if (!this.recordForm.amount || this.recordForm.amount <= 0) {
        alert('请输入正确的金额');
        return;
      }
      const params = {
        userId: this.userId,
        ...this.recordForm
      };
      try {
        const res = await axios.post('/transaction/add', params);
        if (res.data.success) {
          alert(res.data.msg);
          // 重置表单
          this.recordForm = {
            type: 'expense',
            amount: null,
            category: '餐饮',
            date: new Date().toISOString().split('T')[0],
            note: ''
          };
          // 刷新数据
          await this.initData();
        }
      } catch (e) {
        alert('保存失败');
      }
    },
    // 删除流水记录
    async deleteRecord(id) {
      if (!confirm('确定要删除这条记录吗？')) return;
      try {
        const res = await axios.delete('/transaction/delete', {
          params: { id: id }
        });
        if (res.data.success) {
          alert(res.data.msg);
          await this.initData();
        }
      } catch (e) {
        alert('删除失败');
      }
    },
    // 计算收支总额
    calcTotal() {
      const currentMonth = new Date().toISOString().slice(0,7);
      const monthRecords = this.recordList.filter(item => item.date.startsWith(currentMonth));
      this.totalIncome = monthRecords
        .filter(item => item.type === 'income')
        .reduce((sum, item) => sum + Number(item.amount), 0);
      this.totalExpense = monthRecords
        .filter(item => item.type === 'expense')
        .reduce((sum, item) => sum + Number(item.amount), 0);
      // 计算分类支出
      this.categoryExpense = monthRecords
        .filter(item => item.type === 'expense' && (this.budgetForm.category === '全部分类' || item.category === this.budgetForm.category))
        .reduce((sum, item) => sum + Number(item.amount), 0);
    },
    // 初始化图表
    initCharts() {
      if (!this.pieChart) {
        this.pieChart = echarts.init(document.getElementById('categoryPie'));
      }
      if (!this.lineChart) {
        this.lineChart = echarts.init(document.getElementById('monthLine'));
      }
      // 窗口大小变化自适应
      window.addEventListener('resize', () => {
        this.pieChart?.resize();
        this.lineChart?.resize();
      });
    },
    // 获取统计数据并渲染图表
    async getStatisticData() {
      const currentMonth = new Date().toISOString().slice(0,7);
      try {
        // 分类统计数据
        const categoryRes = await axios.get('/statistic/category', {
          params: { userId: this.userId, month: currentMonth }
        });
        const pieData = categoryRes.data.map(item => ({
          name: item.category,
          value: item.amount
        }));
        // 饼图配置
        this.pieChart.setOption({
          tooltip: {
            trigger: 'item',
            formatter: '{b}: {c}元 ({d}%)'
          },
          legend: {
            orient: 'vertical',
            right: 10,
            top: 'center'
          },
          series: [
            {
              name: '支出分类',
              type: 'pie',
              radius: ['40%', '70%'],
              center: ['40%', '50%'],
              data: pieData,
              emphasis: {
                itemStyle: {
                  shadowBlur: 10,
                  shadowOffsetX: 0,
                  shadowColor: 'rgba(0, 0, 0, 0.5)'
                }
              }
            }
          ]
        });

        // 月度趋势数据
        const monthRes = await axios.get('/statistic/month', {
          params: { userId: this.userId }
        });
        const dateList = monthRes.data.map(item => item.date);
        const incomeList = monthRes.data.map(item => item.income);
        const expenseList = monthRes.data.map(item => item.expense);
        // 折线图配置
        this.lineChart.setOption({
          tooltip: {
            trigger: 'axis'
          },
          legend: {
            data: ['收入', '支出'],
            top: 10
          },
          grid: {
            left: '3%',
            right: '4%',
            bottom: '3%',
            containLabel: true
          },
          xAxis: {
            type: 'category',
            boundaryGap: false,
            data: dateList
          },
          yAxis: {
            type: 'value'
          },
          series: [
            {
              name: '收入',
              type: 'line',
              data: incomeList,
              itemStyle: { color: '#00b42a' },
              lineStyle: { color: '#00b42a' }
            },
            {
              name: '支出',
              type: 'line',
              data: expenseList,
              itemStyle: { color: '#f53f3f' },
              lineStyle: { color: '#f53f3f' }
            }
          ]
        });
      } catch (e) {
        console.error('获取统计数据失败', e);
      }
    },
    // 设置预算
    async setBudget() {
      if (!this.budgetForm.monthLimit || this.budgetForm.monthLimit <= 0) {
        alert('请输入正确的预算限额');
        return;
      }
      const params = {
        userId: this.userId,
        ...this.budgetForm
      };
      try {
        await axios.post('/budget/set', params);
        alert('预算设置成功');
        await this.getBudget();
      } catch (e) {
        alert('设置失败');
      }
    },
    // 获取预算
    async getBudget() {
      try {
        const res = await axios.get('/budget/get', {
          params: {
            userId: this.userId,
            month: this.budgetForm.month
          }
        });
        this.budgetLimit = res.data.monthLimit || 0;
        this.calcTotal();
      } catch (e) {
        console.error('获取预算失败', e);
      }
    }
  },
  // 计算预算进度百分比
  computed: {
    budgetPercent() {
      if (this.budgetLimit <= 0) return 0;
      return (this.categoryExpense / this.budgetLimit) * 100;
    }
  }
});