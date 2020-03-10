package com.example.super_movie.controller;


import com.example.super_movie.service.IMovieCommentService;
import com.example.super_movie.service.IReplyOfCommentService;
import com.example.super_movie.util.FileNameUtils;
import com.example.super_movie.util.FileUtils;
import com.example.super_movie.vo.EditorImg;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author earun
 * @since 2020-01-26
 */
@Controller
public class MovieCommentController{
    private final ResourceLoader resourceLoader;

    @Autowired
    public MovieCommentController(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Autowired
    IMovieCommentService movieCommentService;

    @Autowired
    IReplyOfCommentService replyOfCommentService;

    @Value("${web.upload-path}")
    private String path;

    @RequestMapping("postMovieComment")
    @ResponseBody
    public int postMovieComment(Integer userId,String content,String title,Integer movieId){
        return movieCommentService.postMovieComment(userId, content,title,movieId);
    }

    //获取影评页面
    @RequestMapping("movieComment")
    public String toMovieCommentPage(Model model, Integer id,Integer order){
        model.addAttribute("movieComment",movieCommentService.getById(id));
        model.addAttribute("page",replyOfCommentService.getPageNum(id));
        model.addAttribute("replyList",replyOfCommentService.getReplyOfCommentByIdAndPage(id,1,order));
        model.addAttribute("order",order);
        return "movieComment";
    }
    @ResponseBody
    @RequestMapping("upload")
    public EditorImg upload(@RequestParam("fileName") MultipartFile file){

        // 要上传的目标文件存放路径
        String localPath = path;
        EditorImg editorImg=new EditorImg();
        String fileName= FileNameUtils.getFileName(file.getOriginalFilename());
        System.out.println(fileName);
        String[] imgData={"/show?fileName="+fileName};
        // 上传成功或者失败的提示
        String msg = "";

        if (FileUtils.upload(file, localPath, fileName)){
            // 上传成功，给出页面提示
            editorImg.setErrno(0);
            editorImg.setData(imgData);
            msg = "上传成功！";
        }else {
            editorImg.setErrno(1);
            msg = "上传失败！";

        }

        // 显示图片

        return editorImg;
    }

    @RequestMapping("show")
    public ResponseEntity showPhotos(String fileName){

        try {
            // 由于是读取本机的文件，file是一定要加上的， path是在application配置文件中的路径
            return ResponseEntity.ok(resourceLoader.getResource("file:" + path + fileName));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

}
