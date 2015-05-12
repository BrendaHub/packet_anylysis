package hunkann;


import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;


class TreeFrame extends JFrame{
	
	private void createNodes(DefaultMutableTreeNode root){
		DefaultMutableTreeNode mac_frame = new DefaultMutableTreeNode(String.format("mac帧 长度： %d", 93));
		DefaultMutableTreeNode mac_src = new DefaultMutableTreeNode("mac_src");
		DefaultMutableTreeNode mac_dst = new DefaultMutableTreeNode("mac_dst");
		DefaultMutableTreeNode mac_len = new DefaultMutableTreeNode("mac_len");
		mac_frame.add(mac_src);
		mac_frame.add(mac_dst);
		mac_frame.add(mac_len);
		DefaultMutableTreeNode ip_pkt = new DefaultMutableTreeNode(String.format("ip包 长度： %d", 43));
		DefaultMutableTreeNode ip_src = new DefaultMutableTreeNode("ip_src");
		DefaultMutableTreeNode ip_dst = new DefaultMutableTreeNode("ip_dst");
		DefaultMutableTreeNode ip_len = new DefaultMutableTreeNode("ip_len");
		ip_pkt.add(ip_src);
		ip_pkt.add(ip_dst);
		ip_pkt.add(ip_len);
		root.add(mac_frame);
		root.add(ip_pkt);
	}
	
	public TreeFrame(){
		setSize(300,200);
		int index = 0;
		DefaultMutableTreeNode root = new DefaultMutableTreeNode(String.format("抓到的第 %d 个包!", index));
		createNodes(root);
		JTree tree = new JTree(root);
		add(tree);
	}
}

public class TestJTree {
	public static void main(String[] args) {
		TreeFrame frame = new TreeFrame();
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}
