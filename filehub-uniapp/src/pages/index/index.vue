<template>
	<view style="width: 100%;">

		<view style="width: 100%;display: flex;justify-content: center;">

			<view style="margin-bottom: 4px;">
				<uni-title type="h1" title="用户信息统计"></uni-title>
			</view>

		</view>

		<view class="user-list">
			<block v-for="(user, index) in userTableData" :key="index">
				<uni-card :title="user.accessKey" extra="AccessKey">
					<view class="user-item">
						<!-- 使用grid布局 -->
						<view class="user-grid">
							<text class="label">创建时间：</text>
							<text>{{ formatDate(user.createTime) }}</text>
						</view>
						<view class="user-grid">
							<text class="label">有效时间：</text>
							<text>{{ ((user.validTime) === -1) ? '🤩' : user.validTime }} 分钟</text>
						</view>
						<view class="user-grid">
							<text class="label">剩余时间：</text>
							<text>{{ getRemainingTime(user) }} 分钟</text>
						</view>
						<view class="user-grid">
							<text class="label">可用容量：</text>
							<text>{{ user.availableSpace }} KB （{{ (user.availableSpace / 1024).toFixed(2) }} MB）</text>
						</view>
						<view class="user-grid" style="display: flex;justify-content: center;margin-top: 4px;">
							<view style="
								background-color: hotpink;
								width: 32%;
								border-radius: 4px;
								display: flex;
								justify-content: center;
								padding: 4px 0;
								box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1), 0 3px 10px rgba(0, 0, 0, 0.19);
							" @click="handleDeleteUser(user.accessKey)">
								<text style="
									color: aliceblue;
									letter-spacing: 1px;
								">删除密钥️</text>
							</view>
						</view>
					</view>
				</uni-card>
			</block>
		</view>

		<view style="display: flex;justify-content: center;text-align: center;">
			<view style="
				margin-left: auto;
				margin-right: auto;
				margin-top: 18px;
				margin-bottom: 24px;
				width: 48%;
				padding: 8px;
				background-color: rgb(141, 84,255);
				border-radius: 8px;
				box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1), 0 6px 20px rgba(0, 0, 0, 0.19);
			" @click="handleReflash">
				<text style="font-weight: 600;color: aliceblue; letter-spacing: 2px;">刷新数据🐌</text>
			</view>
		</view>
	</view>
</template>

<script setup lang="ts">
	import { computed, onMounted, ref } from 'vue';
	import type { DeleteUserParams, userData } from '@/api/types/type';
	import { deleteUserService, getUserListService } from '@/api/service/user';
import { onPullDownRefresh } from '@dcloudio/uni-app';

	const userTableData = ref<userData[]>([])

	const deleteUserData = ref<DeleteUserParams>({
		accessKey: ""
	})

	const formatDate = (dateString : string) : string => {
		const date = new Date(dateString);
		const year = date.getFullYear();
		const month = String(date.getMonth() + 1).padStart(2, '0');
		const day = String(date.getDate()).padStart(2, '0');
		const hours = String(date.getHours()).padStart(2, '0');
		const minutes = String(date.getMinutes()).padStart(2, '0');
		const seconds = String(date.getSeconds()).padStart(2, '0');

		return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`;
	}

	const getRemainingTime = (user : userData) : string => {
		if (user.validTime === -1) return '♾️'; // 永久有效

		const createTime = new Date(user.createTime).getTime(); // 创建时间的时间戳（毫秒）
		const currentTime = Date.now(); // 当前时间的时间戳（毫秒）
		const validDuration = user.validTime * 60 * 1000; // 有效时长转换为毫秒

		let remainingTime = Math.max(0, Math.round((createTime + validDuration - currentTime) / 60000));
		return remainingTime.toString();
	}

	const getUserTableData = async () => {
		uni.showLoading({
			title: '加载用户数据'
		})
		let result = await getUserListService()
		userTableData.value = result.data as userData[];
		uni.hideLoading()
	}

	onMounted(() => {
		getUserTableData();
	})


	const handleReflash = () => {
		getUserTableData();
	}
	
	onPullDownRefresh(()=> {
		handleReflash()
	})
	
	const handleDeleteUser = (accessKey: string) => {
		uni.showModal({
			title: '确认删除?',
			content: accessKey,
			success: async function (res) {
				if (res.confirm) {
					uni.showLoading({
						title: '正在删除' + accessKey
					})
					deleteUserData.value.accessKey = accessKey
					await deleteUserService(deleteUserData.value)
					uni.hideLoading()
					handleReflash()
				} else if (res.cancel) {
					
				}
			}
		});

	}
</script>
<style scoped>

</style>