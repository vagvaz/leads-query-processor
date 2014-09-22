/*
_______________________
Apatar Open Source Data Integration
Copyright (C) 2005-2007, Apatar, Inc.
info@apatar.com
195 Meadow St., 2nd Floor
Chicopee, MA 01013

��� This program is free software; you can redistribute it and/or modify
��� it under the terms of the GNU General Public License as published by
��� the Free Software Foundation; either version 2 of the License, or
��� (at your option) any later version.

��� This program is distributed in the hope that it will be useful,
��� but WITHOUT ANY WARRANTY; without even the implied warranty of
��� MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.� See the
��� GNU General Public License for more details.

��� You should have received a copy of the GNU General Public License along
��� with this program; if not, write to the Free Software Foundation, Inc.,
��� 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
________________________

 */

package com.apatar.functions.String;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Properties;

import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.internet.MimeMultipart;

import com.apatar.core.AbstractApatarFunction;
import com.apatar.functions.FunctionInfo;
import com.apatar.ui.FunctionCategory;

public class ParseEmailBodyTransformFunction extends AbstractApatarFunction {

	private String contentHtml = "";
	private String contentPlain = "";

	public Object execute(List list) {

		if (list.get(0) == null) {
			return null;
		}
		Properties prop = new Properties();
		Session sess = Session.getDefaultInstance(prop, null);
		contentHtml = "";
		contentPlain = "";
		if (list.size() == 1) {

			InputStream stream = new java.io.ByteArrayInputStream(list.get(0)
					.toString().getBytes());
			javax.mail.internet.MimeMessage message;
			try {
				message = new javax.mail.internet.MimeMessage(sess, stream);

				Object cnt = message.getContent();
				MimeMultipart content;
				if (cnt instanceof String) {
					contentPlain = (String) cnt;
				} else {
					if (cnt instanceof com.sun.mail.util.BASE64DecoderStream) {
						System.out.println(System
								.getProperty("mail.mime.base64.ignoreerrors"));
						System.setProperty("mail.mime.base64.ignoreerrors",
								"true");
						System.out.println(System
								.getProperty("mail.mime.base64.ignoreerrors"));
						String mess = "";
						try {
							InputStream is = (InputStream) cnt;
							int ch = -1;
							while ((ch = is.read()) != -1) {
								mess += ((char) ch);
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
						System.err.println(mess);
						content = new MimeMultipart(mess);
					} else {
						content = (MimeMultipart) cnt;
					}
					BodyPart body = content.getBodyPart(0);
					dumpPart(body);
				}

				if ("".equals(contentPlain) && "".equals(contentHtml)) {
					InputStream is = message.getInputStream();
					BufferedReader reader = new BufferedReader(
							new InputStreamReader(is));
					String thisLine = reader.readLine();
					while (thisLine != null) {
						contentPlain += thisLine;
						thisLine = reader.readLine();
					}
				} else if ("".equals(contentPlain) && !"".equals(contentHtml)) {
					return contentHtml;
				}
				return contentPlain;
			} catch (MessagingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return list.get(0);

	}

	/*
	 * TODO move this method to the ApatarMailUtilities to core package and
	 * update implementation of this function
	 */
	private void dumpPart(Part p) throws Exception {
		if (p.isMimeType("text/plain")) {
			// contentPlain = "";
			// String cp = System.getProperty("console.encoding", "latin1");
			// String con = (String) p.getContent();
			// String enc = new String(Base64.decode(con));

			contentPlain += (String) p.getContent();
			// contentPlain = new String(contentPlain.getBytes("US-ASCII"),
			// "latin1");
		} else if (p.isMimeType("text/html")) {
			contentHtml += (String) p.getContent();
		} else if (p.isMimeType("multipart/*")) {
			Multipart mp = (Multipart) p.getContent();
			int count = mp.getCount();
			for (int i = 0; i < count; i++) {
				dumpPart(mp.getBodyPart(i));
			}
		}
	}

	static FunctionInfo fi = new FunctionInfo("Parse Email Body", 1, 1);
	static {
		fi.getCategories().add(FunctionCategory.String);
		fi.getCategories().add(FunctionCategory.ALL);
	}

	public FunctionInfo getFunctionInfo() {
		return fi;
	}

}
