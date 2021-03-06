<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>CKC Plugin Demo</title>
    <script src="${pageContext.request.contextPath}/scripts/ckeditor/ckeditor.js" type="text/javascript"></script>
    <script type="text/javascript">
      function onLoad(event) {
        var CONTEXTPATH = "${pageContext.request.contextPath}";
        
        CKEDITOR.plugins.addExternal('ckc', CONTEXTPATH + '/scripts/ckplugins/ckc/');
        CKEDITOR.replace("sample", {
          skin: 'demo,' + CONTEXTPATH + '/ckskins/demo/',
          height: '400px',
          toolbar: [
            { name: 'document', items : [ 'CKCSave', 'CKCRevert', 'DocProps', 'Templates' ] },
            { name: 'clipboard', items : [ 'Cut','Copy','Paste','PasteText','PasteFromWord','-','Undo','Redo' ] },
            { name: 'editing', items : [ 'Find','Replace','-','SelectAll','-', 'Scayt' ] },
            { name: 'paragraph', items : [ 'NumberedList','BulletedList','-','Outdent','Indent','-','Blockquote', ,'-','JustifyLeft','JustifyCenter','JustifyRight','JustifyBlock','-','BidiLtr','BidiRtl' ] },
            { name: 'links', items : [ 'Link','Unlink','Anchor' ] },
            { name: 'insert', items : [ 'Image', 'Table','HorizontalRule', 'SpecialChar','PageBreak' ] },
            { name: 'styles', items : [ 'Styles','Format','Font','FontSize' ] },
            { name: 'basicstyles', items : [ 'Bold','Italic','Underline','Strike','Subscript','Superscript','-','RemoveFormat' ] },
            { name: 'colors', items : [ 'TextColor','BGColor' ] },
            { name: 'tools', items : [ 'ShowBlocks', 'Maximize' ] }
          ],
          fullPage : true,
          extraPlugins: 'docprops,ckc',
          ckc: {
            documentId: ${document.key.id},
            updateInterval: 500,
            connectorUrl: CONTEXTPATH + '/ckc/'
          }
        });
      }
    </script>
  </head>
  <body onload="onLoad(event);">
    <h2>Welcome to CKC plugin demo!</h2>
    <i>This demo uses Java connector. You can find more information about connectors from configuration documentation: <a href="http://code.google.com/p/ckc-plugin/wiki/Configuartion">http://code.google.com/p/ckc-plugin/wiki/Configuartion</a></i>
    <p>CKC plugin is a plugin for CKEditor (<a href="http://ckeditor.com">http://ckeditor.com</a>) that enables CKEditor to be edited simultaneously by several users.</p>
    <p>You can find more information about this project from the project page at: <a href="http://http://code.google.com/p/ckc-plugin">http://code.google.com/p/ckc-plugin</a></p>
    <textarea name="sample"></textarea>
  </body>
</html>