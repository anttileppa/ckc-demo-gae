<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Insert title here</title>
    <script src="${pageContext.request.contextPath}/scripts/ckeditor/ckeditor.js" type="text/javascript"></script>
    <script type="text/javascript">
      function onLoad(event) {
        var CONTEXTPATH = "${pageContext.request.contextPath}";
        
        CKEDITOR.plugins.addExternal('ckc', CONTEXTPATH + '/scripts/ckplugins/ckc/');

        CKEDITOR.replace("sample", {
          extraPlugins: 'ckc',
          ckc: {
            documentId: ${document.key.id},
            originalRevision: ${document.revisionNumber},
            updateInterval: 500,
            connectorUrl: CONTEXTPATH + '/ckc/'
          }
        });
      }
    </script>
  </head>
  <body onload="onLoad(event);">
    <h2>Welcome!</h2>
    
    <textarea name="sample">${document.data}</textarea>
  </body>
</html>