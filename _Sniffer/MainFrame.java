package _Sniffer;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.*;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;

import jpcap.*;
import jpcap.packet.ARPPacket;
import jpcap.packet.DatalinkPacket;
import jpcap.packet.EthernetPacket;
import jpcap.packet.ICMPPacket;
import jpcap.packet.IPPacket;
import jpcap.packet.Packet;
import jpcap.packet.TCPPacket;
import jpcap.packet.UDPPacket;

public class MainFrame extends javax.swing.JFrame {

	@SuppressWarnings("unused")
	private class PacketPrinter implements PacketReceiver {
		// this method is called every time Jpcap captures a packet
		public void receivePacket(Packet packet) {
			// just print out a captured packet
			packet_arr.add(packet);
			listItems.addElement(packet);
		}
	}

	String getFormatedItemStr(Packet pkt) {
		StringBuilder result = new StringBuilder();
		int count = 0;
		for (byte b : pkt.header) {
			if (count % 16 == 0) {
				result.append(String.format("%05x:   ", count));
			}
			result.append(String.format("%02x ", b));
			++count;
			if (count % 16 == 0) {
				result.append("\n");
			}
		}
		for (byte b : pkt.data) {
			if (count % 16 == 0) {
				result.append(String.format("%05x:   ", count));
			}
			result.append(String.format("%02x ", b));
			++count;
			if (count % 16 == 0) {
				result.append("\n");
			}
		}
		result.append("\n");
		return result.toString();
	}

	private class TaskThread implements Runnable {
		
		String formatItem(Packet pkt){
			StringBuilder result = new StringBuilder();
			// 编号
			result.append(++packet_count).append("        ");
			// 到达时间
			SimpleDateFormat sDateFormat = new SimpleDateFormat(
					"HH:mm:ss:SS");
			String arrivetime = sDateFormat.format(new Date(System
					.currentTimeMillis()));
			result.append(arrivetime).append("        ");
			
			// 源地址    目的地址   协议类型  长度
			if(pkt.getClass().equals(ARPPacket.class)){
				ARPPacket arp_pkt = (ARPPacket)pkt;
				result.append(arp_pkt.getSenderHardwareAddress().toString());
				result.append("    --->    ");
				result.append(arp_pkt.getTargetHardwareAddress().toString());
				result.append("            ");
				result.append("ARP     ");
				result.append(arp_pkt.hlen);
			}else if(pkt.getClass().equals(UDPPacket.class)){
				UDPPacket udp_pkt = (UDPPacket)pkt;
				result.append(udp_pkt.src_ip);
				result.append("    --->    ");
				result.append(udp_pkt.dst_ip);
				result.append("            ");
				result.append("UDP     ");
				result.append(udp_pkt.length);
			}else if(pkt.getClass().equals(TCPPacket.class)){
				TCPPacket tcp_pkt = (TCPPacket)pkt;
				result.append(tcp_pkt.src_ip);
				result.append("    --->    ");
				result.append(tcp_pkt.dst_ip);
				result.append("            ");
				result.append("TCP    ");
				result.append(tcp_pkt.length);
			}else if(pkt.getClass().equals(ICMPPacket.class)){
				// TODO  其他的包.....
				result.append("  ICMP  ");
			}else if(pkt.header[14+9]==2){
				result.append("  IGMP  ");
			}
			return result.toString();
		}
		/**
		 * 把过滤器复选框中的字段拿出来合成一个过滤器
		 * @return
		 */
		String getFilterName(){
			StringBuilder res = new StringBuilder();
			if(jCheckBox_ip.isSelected()){
				res.append("ip or ");
			}
			if(jCheckBoxTcp.isSelected()){
				res.append("tcp or ");
			}
			if(jCheckBox_arp.isSelected()){
				res.append("arp or ");
			}
			if(jCheckBox_udp.isSelected()){
				res.append("udp or ");
			}
			if(jCheckBox_ipv6.isSelected()){
				res.append("ip6 or ");
			}
			if(jCheckBox_icmp6.isSelected()){
				res.append("icmp6 or ");
			}
//			if(jCheckBox_icmp.isSelected()){
//				res.append("icmp or");
//			}
			// TODO 添加完整的 ... 
			String str = " ";
			if(res.length()>4)
			  str = res.substring(0,res.length()-4);
			return str.trim();
		}
		
		public void run() {
			// 获取选择的设备的下标
			int index = jComboBox_devices.getSelectedIndex();
			try {
				captor = JpcapCaptor
						.openDevice(devices[index], 65535, true, 20);
				// TODO 设置过滤器
				String filter_name = getFilterName();
				if(filter_name.length()!=0){
					captor.setFilter(filter_name, true);
				}
				while (_continue) {
					Packet new_pkt = captor.getPacket();
					if (new_pkt != null
							&& ((EthernetPacket) new_pkt.datalink).frametype > 0) {
						// 显示抓到的包
						
						listItems.addElement(formatItem(new_pkt));
						// 把包存起来
						packet_arr.add(new_pkt);
					}
					Thread.sleep(10);
				}
				// captor.processPacket(10, new PacketPrinter());
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}

	private class StartAction implements ActionListener {
		// 开始按钮的事件;
		public void actionPerformed(ActionEvent event) {
			// 创建一个线程用来和界面的线程分开(不分开的话界面抢占不到cpu就无法更新界面");
			thread = new Thread(new TaskThread());
			_continue = true;
			thread.start();
		}
	}

	private class StopAction implements ActionListener {
		// 关闭按钮的事件;
		public void actionPerformed(ActionEvent event) {
			_continue = false;
			// thread.suspend();
			captor.close();
		}
	}

	String formatMacAddress(byte[] addr) {
		// 格式化 mac 地址;
		StringBuilder result = new StringBuilder();
		for (byte b : addr) {
			result.append(String.format("%02x : ", b));
		}
		return result.toString();
	}

	String getHardwareType(short type) {
		switch (type) {
		case ARPPacket.HARDTYPE_ETHER:
			return "以太网 10MB";
		case ARPPacket.HARDTYPE_FRAMERELAY:
			return "FRAME RELAY";
		case ARPPacket.HARDTYPE_IEEE802:
			return "IEEE802 令牌环";
		case 2:
			return "以太网 3MB";
		case 3:
			return "业余无线电 AX.25";
		case 5:
			return "CHAOSnet";
		case 7:
			return "ARCNET";
		default:
			return "unkown";
		}
	}

	String getProtoType(short type) {
		switch (type) {
		case ARPPacket.PROTOTYPE_IP:
			return "IP";
		default:
			return "unknown";
		}
	}

	String getOperationType(short type) {
		switch (type) {
		case ARPPacket.ARP_REQUEST:
			return " arp 请求";
		case ARPPacket.ARP_REPLY:
			return " arp 回复";
		case ARPPacket.RARP_REQUEST:
			return " rarp 请求 ";
		case ARPPacket.RARP_REPLY:
			return " rarp 回复";
		case ARPPacket.INV_REQUEST:
			return "identify peer 请求";
		case ARPPacket.INV_REPLY:
			return "identify peer 回复";
		default:
			return "unknown";
		}
	}

	String getIPVersion(byte version) {
		return version == 4 ? "ipv4" : "ipv6";
	}

	void addIPServType(byte type, DefaultMutableTreeNode root) {
		// 服务类型（Type of Service）：长度8比特。8位 按位被如下定义 PPP DTRC0
		// PPP：定义包的优先级，取值越大数据越重要
		// 000 普通 (Routine)
		// 001 优先的 (Priority)
		// 010 立即的发送 (Immediate)
		// 011 闪电式的 (Flash)
		// 100 比闪电还闪电式的 (Flash Override)
		// 101 CRI/TIC/ECP(找不到这个词的翻译)
		// 110 网间控制 (Internetwork Control)
		// 111 网络控制 (Network Control)
		// 优先级
		String priority = null;
		switch ((type & 0x1f) >> 5) {
		case 0:
			priority = new String("Routine");
			break;
		case 1:
			priority = new String("Priority");
			break;
		case 2:
			priority = new String("Immediate");
			break;
		case 3:
			priority = new String("Flash");
			break;
		case 4:
			priority = new String("Flash Override");
			break;
		case 5:
			priority = new String("CRI/TIC/ECP");
			break;
		case 6:
			priority = new String("Internetwork Contorl");
			break;
		case 7:
			priority = new String("Network Control");
			break;
		default:
			priority = new String("unknown");
		}
		DefaultMutableTreeNode pri_node = new DefaultMutableTreeNode("优先级  ： "+priority);
		root.add(pri_node);

		// D 时延: 0:普通 1:延迟尽量小
		// T 吞吐量: 0:普通 1:流量尽量大
		// R 可靠性: 0:普通 1:可靠性尽量大
		// M 传输成本: 0:普通 1:成本尽量小
		// 0 最后一位被保留，恒定为0

		// 延时
		DefaultMutableTreeNode delay_node = new DefaultMutableTreeNode("延时: "
				+ (((type & 0xef) >> 4 == 1) ? "延时尽量小" : "普通"));
		root.add(delay_node);
		// 吞吐量
		DefaultMutableTreeNode thruput_node = new DefaultMutableTreeNode(
				"吞吐量: " + (((type & 0xf7) >> 3 == 1) ? "流量尽量大" : "普通"));
		root.add(thruput_node);
		DefaultMutableTreeNode reliability_node = new DefaultMutableTreeNode(
				"可靠性: " + (((type & 0xfb) >> 2 == 1) ? "尽量可靠" : "普通"));
		root.add(reliability_node);
	}

	// 构造树
	void generateTree(Packet pkt) {
		SimpleDateFormat sDateFormat = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss:SS");
		String arrivetime = sDateFormat.format(new Date(System
				.currentTimeMillis()));
		DefaultMutableTreeNode root = new DefaultMutableTreeNode(String.format(
				"数据包长度 : %d ,捕获的长度 : %d , 捕获时间: %s", pkt.len, pkt.caplen,
				arrivetime));
		// 构造mac帧节点
		EthernetPacket mac_frame = (EthernetPacket) pkt.datalink;
		DefaultMutableTreeNode mac_root = new DefaultMutableTreeNode("-mac帧信息");
		DefaultMutableTreeNode mac_src = new DefaultMutableTreeNode(
				String.format("mac 源地址 ：%s ",
						formatMacAddress(mac_frame.src_mac)));
		DefaultMutableTreeNode mac_dst = new DefaultMutableTreeNode(
				String.format("mac 目的地址 ：%s ",
						formatMacAddress(mac_frame.dst_mac)));
		DefaultMutableTreeNode mac_type = new DefaultMutableTreeNode(
				String.format("链路层协议类型：%s ",
						mac_frame.frametype + ""));
		mac_root.add(mac_src);
		mac_root.add(mac_dst);
		mac_root.add(mac_type);
		root.add(mac_root);
		// 构造各种协议节点

		if (pkt.getClass().equals(ARPPacket.class)) {
			// 是arp报文
			DefaultMutableTreeNode new_root = new DefaultMutableTreeNode(
					"arp报文信息");
			ARPPacket arp_pkt = (ARPPacket) pkt;
			DefaultMutableTreeNode proto_type = new DefaultMutableTreeNode(
					"协议类型  : " + getProtoType(arp_pkt.prototype));
			DefaultMutableTreeNode hard_type = new DefaultMutableTreeNode(
					"硬件类型 : " + getHardwareType(arp_pkt.hardtype));
			DefaultMutableTreeNode operation_type = new DefaultMutableTreeNode(
					"操作类型  : " + getOperationType(arp_pkt.hardtype));
			DefaultMutableTreeNode proto_addr_len = new DefaultMutableTreeNode(
					"协议地址长度 : " + arp_pkt.plen + " Bytes");
			DefaultMutableTreeNode hardware_addr_len = new DefaultMutableTreeNode(
					"硬件地址长度  : " + arp_pkt.hlen + " Bytes");
			DefaultMutableTreeNode send_haddr = new DefaultMutableTreeNode(
					"源 MAC地址  : " + arp_pkt.getSenderHardwareAddress());
			DefaultMutableTreeNode send_addr = new DefaultMutableTreeNode(
					"源协议地址   : " + arp_pkt.getSenderProtocolAddress());
			DefaultMutableTreeNode trg_haddr = new DefaultMutableTreeNode(
					"目的MAC地址   : " + arp_pkt.getTargetHardwareAddress());
			DefaultMutableTreeNode trg_addr = new DefaultMutableTreeNode(
					"目的协议地址   : " + arp_pkt.getTargetProtocolAddress());
			new_root.add(proto_type);
			new_root.add(hard_type);
			new_root.add(operation_type);
			new_root.add(proto_addr_len);
			new_root.add(hardware_addr_len);
			new_root.add(send_haddr);
			new_root.add(trg_haddr);
			new_root.add(send_addr);
			new_root.add(trg_addr);
			root.add(new_root);
		} else {
			// 是ip数据报
			DefaultMutableTreeNode ip_root = new DefaultMutableTreeNode(
					"IP报文信息");
			IPPacket ip_pkt = (IPPacket) pkt;
			DefaultMutableTreeNode version_node = new DefaultMutableTreeNode(
					"IP version  : " + getIPVersion(ip_pkt.version));
			ip_root.add(version_node);
			if (ip_pkt.version == 4) {
				// ip v4 处理方法
				// 这里ip_pkt.header[14]是因为mac帧头部有14个字节（6 src_mac+6 dst_mac+2 type)
				DefaultMutableTreeNode head_len = new DefaultMutableTreeNode("IP head length : "+
						(ip_pkt.header[14] & 0x0f) * 4 + " bytes"); 
				ip_root.add(head_len);
				DefaultMutableTreeNode serve_type = new DefaultMutableTreeNode(
						"Differentiated Services : ");
				addIPServType(ip_pkt.rsv_tos, serve_type);
				ip_root.add(serve_type);
				// 总长度（首部+数据）
				DefaultMutableTreeNode packet_len = new DefaultMutableTreeNode(
						"Packet Length : " + ip_pkt.length);
				ip_root.add(packet_len);
				// 标识 identification 为了分片后的数据包重组
				DefaultMutableTreeNode id_node = new DefaultMutableTreeNode(
						String.format("Identification : 0x%x" , ip_pkt.ident));
				ip_root.add(id_node);
				// 标志
				DefaultMutableTreeNode flag_node = new DefaultMutableTreeNode(
						"flag : "+((ip_pkt.dont_frag==true)?"不要分片   ":"允许分片    ") +  ((ip_pkt.more_frag==true)?"还有分片":"最后一个分片") );
				ip_root.add(flag_node);
				// 片偏移
				DefaultMutableTreeNode fragment_offset = new DefaultMutableTreeNode(
						"fragment offset : " + ip_pkt.offset);
				ip_root.add(fragment_offset);
				// 生存时间
				DefaultMutableTreeNode time_to_live = new DefaultMutableTreeNode("Time to live : "+ip_pkt.hop_limit);
				ip_root.add(time_to_live);
				// 协议类型
				DefaultMutableTreeNode protocol_type = new DefaultMutableTreeNode("protocol type : "+getProtocolType(ip_pkt.protocol));
				ip_root.add(protocol_type);
				// 首部校验和 : 只检验数据包首部
				short checksum = ip_pkt.header[11];
				DefaultMutableTreeNode check_sum = new DefaultMutableTreeNode(String.format("check sum : 0x%x", checksum));
				ip_root.add(check_sum);
				// 源地址
				DefaultMutableTreeNode src_addr = new DefaultMutableTreeNode("Source Address : " + ip_pkt.src_ip);
				ip_root.add(src_addr);
				// 目的地址
				DefaultMutableTreeNode dst_addr = new DefaultMutableTreeNode("Destination Address : "+ ip_pkt.dst_ip);
				ip_root.add(dst_addr);
				// 可选字段
				DefaultMutableTreeNode opt_node = new DefaultMutableTreeNode(" option : " + ip_pkt.option);
				ip_root.add(opt_node);
				
			} else {
				// TODO ipv6 处理方法
			}
			root.add(ip_root);
			// 解析运输层协议
			if(_is_udp_pkt){
				// 解析udp报文
				DefaultMutableTreeNode udp_root = new DefaultMutableTreeNode("UDP 报文信息");
				UDPPacket udp_pkt = (UDPPacket)pkt;
				// 源端口
				DefaultMutableTreeNode src_port = new DefaultMutableTreeNode("源端口 ： "+ udp_pkt.src_port);
				udp_root.add(src_port);
				// 目的端口
				DefaultMutableTreeNode dst_port = new DefaultMutableTreeNode("目的端口 : " + udp_pkt.dst_port);
				udp_root.add(dst_port);
				// 长度
				DefaultMutableTreeNode len_node = new DefaultMutableTreeNode("长度 : "+udp_pkt.length);
				udp_root.add(len_node);
				// 检验和
				DefaultMutableTreeNode check_sum = new DefaultMutableTreeNode(
						String.format("检验和  : 0x%x", udp_pkt.header[26]));
				udp_root.add(check_sum);
				root.add(udp_root);
				_is_udp_pkt = false;
			}
			if(_is_tcp_pkt){
				// 解析tcp报文
				DefaultMutableTreeNode tcp_root = new DefaultMutableTreeNode("TCP 报文信息");
				TCPPacket tcp_pkt = (TCPPacket)pkt;
				
				// 源端口
				DefaultMutableTreeNode src_port = new DefaultMutableTreeNode("源端口 ： "+ tcp_pkt.src_port);
				tcp_root.add(src_port);
				// 目的端口
				DefaultMutableTreeNode dst_port = new DefaultMutableTreeNode("目的端口 : " + tcp_pkt.dst_port);
				tcp_root.add(dst_port);
				
				// TODO tcp其他信息 。。。。。。
				
				
				root.add(tcp_root);
				_is_tcp_pkt = false;
			}
			// TODO 其他协议的解析.....
			
		}
		jTree_PacketInfo = new JTree(root);
		jScrollPanel_packetInfo.setViewportView(jTree_PacketInfo);
	}
	
	/** 返回ip数据包中协议类型字段的自然语言表达
	 * @param type 协议类型
	 * @return
	 */
	String getProtocolType(short type){
		switch(type){
		case IPPacket.IPPROTO_ICMP:
			_is_icmp_pkt = true;
			return "ICMP";
		case IPPacket.IPPROTO_IGMP:
			_is_igmp_pkt = true;
			return "IGMP";
		case IPPacket.IPPROTO_IP:
			return "IP";
		case IPPacket.IPPROTO_TCP:
			_is_tcp_pkt = true;
			return "TCP";
		case IPPacket.IPPROTO_UDP:
			_is_udp_pkt = true;
			return "UDP";
		case IPPacket.IPPROTO_IPv6:
			return "IPv6";
		case IPPacket.IPPROTO_IPv6_ICMP:
			_is_icmpv6_pkt = true;
			return "ICMPv6";
		case 89:
			return "OSPF";
		case 8:
			return "EGP";
		default:
			return "Unknown";
		}
	}

	public MainFrame() {
		initComponents();
	}

	@SuppressWarnings("unchecked")
	private void initComponents() {
		jScrollPanel_packetList = new javax.swing.JScrollPane();
		listItems = new DefaultListModel();
		jList1 = new javax.swing.JList(listItems);
		jCheckBoxTcp = new javax.swing.JCheckBox();
		jCheckBox_udp = new javax.swing.JCheckBox();
		jCheckBox_arp = new javax.swing.JCheckBox();
		jCheckBox_ip = new javax.swing.JCheckBox();
		jCheckBox_ipv6 = new javax.swing.JCheckBox();
		jCheckBox_icmp = new javax.swing.JCheckBox();
		jCheckBox_icmp6 = new javax.swing.JCheckBox();
		jScrollPanel_packetInfo = new javax.swing.JScrollPane();
		jScrollPanel_packetData = new javax.swing.JScrollPane();
		jTextArea_packetData = new javax.swing.JTextArea();
		jButton_start = new javax.swing.JButton();
		jButton_stop = new javax.swing.JButton();
		packet_arr = new ArrayList<Packet>();

		setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

		jList1.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent event) {
				int index = jList1.getSelectedIndex();
				Packet pkt = packet_arr.get(index);
				// 将抓到的包的二进制数据格式化为16进制之后显示到最下面的文本区
				String str = getFormatedItemStr(pkt);
				jTextArea_packetData.setText(str);
				generateTree(pkt);
			}
		});

		jScrollPanel_packetList.setViewportView(jList1);

		jCheckBoxTcp.setText("tcp");
		jCheckBox_udp.setText("udp");
		jCheckBox_arp.setText("arp");
		jCheckBox_ip.setText("ip");
		jCheckBox_ipv6.setText("ip6");
		jCheckBox_icmp.setText("icmp");
		jCheckBox_icmp6.setText("icmp6");

		jTextArea_packetData.setEditable(false);
		jTextArea_packetData.setColumns(20);
		jTextArea_packetData.setRows(5);
		jScrollPanel_packetData.setViewportView(jTextArea_packetData);

		jButton_start.setText("开始");
		jButton_stop.setText("结束");
		// 给按钮注册事件
		jButton_start.addActionListener(new StartAction());
		jButton_stop.addActionListener(new StopAction());

		// 初始化设备列表
		devices = JpcapCaptor.getDeviceList();

		jComboBox_devices = new JComboBox();
		jComboBox_devices.setEditable(false);
		for (int ix = 0; ix < devices.length; ++ix) {
			jComboBox_devices.addItem(ix + ". " + devices[ix].description + " "
					+ devices[ix].datalink_description);
		}

		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(
				getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(layout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(
						layout.createSequentialGroup()
								.addContainerGap()
								.addGroup(
										layout.createParallelGroup(
												javax.swing.GroupLayout.Alignment.LEADING)
												.addComponent(
														jScrollPanel_packetList)
												.addGroup(
														layout.createSequentialGroup()
																.addComponent(
																		jComboBox_devices,
																		javax.swing.GroupLayout.PREFERRED_SIZE,
																		264,
																		javax.swing.GroupLayout.PREFERRED_SIZE)
																.addPreferredGap(
																		javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																.addComponent(
																		jCheckBoxTcp)
																.addPreferredGap(
																		javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
																.addComponent(
																		jCheckBox_ipv6)
																.addGap(18, 18,
																		18)
																.addComponent(
																		jCheckBox_ip)
																.addPreferredGap(
																		javax.swing.LayoutStyle.ComponentPlacement.RELATED,
																		8,
																		Short.MAX_VALUE)
																.addComponent(
																		jCheckBox_udp)
																.addPreferredGap(
																		javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
																.addComponent(
																		jCheckBox_icmp)
																.addPreferredGap(
																		javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
																.addComponent(
																		jCheckBox_arp)
																.addPreferredGap(
																		javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
																.addComponent(
																		jCheckBox_icmp6)
																.addGap(18, 18,
																		18)
																.addComponent(
																		jButton_start)
																.addGap(18, 18,
																		18)
																.addComponent(
																		jButton_stop)
																.addGap(78, 78,
																		78))
												.addComponent(
														jScrollPanel_packetInfo)
												.addComponent(
														jScrollPanel_packetData))
								.addContainerGap()));
		layout.setVerticalGroup(layout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(
						layout.createSequentialGroup()
								.addContainerGap()
								.addGroup(
										layout.createParallelGroup(
												javax.swing.GroupLayout.Alignment.LEADING)
												.addGroup(
														layout.createParallelGroup(
																javax.swing.GroupLayout.Alignment.BASELINE)
																.addComponent(
																		jCheckBoxTcp)
																.addComponent(
																		jCheckBox_ipv6)
																.addComponent(
																		jButton_start)
																.addComponent(
																		jButton_stop)
																.addComponent(
																		jCheckBox_ip)
																.addComponent(
																		jComboBox_devices,
																		javax.swing.GroupLayout.PREFERRED_SIZE,
																		javax.swing.GroupLayout.DEFAULT_SIZE,
																		javax.swing.GroupLayout.PREFERRED_SIZE))
												.addGroup(
														layout.createParallelGroup(
																javax.swing.GroupLayout.Alignment.BASELINE)
																.addComponent(
																		jCheckBox_udp)
																.addComponent(
																		jCheckBox_icmp)
																.addComponent(
																		jCheckBox_arp)
																.addComponent(
																		jCheckBox_icmp6)))
								.addPreferredGap(
										javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
								.addComponent(jScrollPanel_packetList,
										javax.swing.GroupLayout.PREFERRED_SIZE,
										232,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(
										javax.swing.LayoutStyle.ComponentPlacement.RELATED)
								.addComponent(jScrollPanel_packetInfo,
										javax.swing.GroupLayout.PREFERRED_SIZE,
										170,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(
										javax.swing.LayoutStyle.ComponentPlacement.RELATED)
								.addComponent(jScrollPanel_packetData,
										javax.swing.GroupLayout.DEFAULT_SIZE,
										171, Short.MAX_VALUE).addContainerGap()));

		pack();
	}

	public static void main(String args[]) {
		try {
			for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager
					.getInstalledLookAndFeels()) {
				if ("Nimbus".equals(info.getName())) {
					javax.swing.UIManager.setLookAndFeel(info.getClassName());
					break;
				}
			}
		} catch (ClassNotFoundException ex) {
			java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(
					java.util.logging.Level.SEVERE, null, ex);
		} catch (InstantiationException ex) {
			java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(
					java.util.logging.Level.SEVERE, null, ex);
		} catch (IllegalAccessException ex) {
			java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(
					java.util.logging.Level.SEVERE, null, ex);
		} catch (javax.swing.UnsupportedLookAndFeelException ex) {
			java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(
					java.util.logging.Level.SEVERE, null, ex);
		}

		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				MainFrame frame = new MainFrame();
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.setVisible(true);
			}
		});

	}

	// Variables declaration - do not modify
	private javax.swing.JButton jButton_start;
	private javax.swing.JButton jButton_stop;
	private javax.swing.JCheckBox jCheckBox_ip;
	private javax.swing.JCheckBox jCheckBoxTcp;
	private javax.swing.JCheckBox jCheckBox_arp;
	private javax.swing.JCheckBox jCheckBox_icmp;
	private javax.swing.JCheckBox jCheckBox_icmp6;
	private javax.swing.JCheckBox jCheckBox_ipv6;
	private javax.swing.JCheckBox jCheckBox_udp;
	private javax.swing.JComboBox jComboBox_devices;
	private javax.swing.JList jList1;
	private DefaultListModel listItems;
	private javax.swing.JScrollPane jScrollPanel_packetData;
	private javax.swing.JScrollPane jScrollPanel_packetInfo;
	private javax.swing.JScrollPane jScrollPanel_packetList;
	private javax.swing.JTextArea jTextArea_packetData;
	private javax.swing.JTree jTree_PacketInfo;
	private jpcap.NetworkInterface[] devices;
	private jpcap.JpcapCaptor captor;
	private Boolean _continue = true;
	private ArrayList<Packet> packet_arr;
	// End of variables declaration
	private Thread thread;
	private static int packet_count = 0;
	private Boolean _is_udp_pkt = false;
	private Boolean _is_tcp_pkt = false;
	private Boolean _is_icmp_pkt = false;
	private Boolean _is_igmp_pkt = false;
	private Boolean _is_icmpv6_pkt = false;
}
