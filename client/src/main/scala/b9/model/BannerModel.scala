package b9.model

/**
  * Created by blu3gui7ar on 2017/5/23.
  */

abstract class Model


case class BannerModel(
                        processor: String = "",
                        tpl: String = "",
                        pattern: String = "",
                        size: SizeModel = SizeModel(),
                        time: TimeModel = TimeModel(),
                        urlRoot: String = "",
                        imgs: Seq[ImgModel] = Seq.empty[ImgModel],
                        effect: EffectModel = EffectModel()
                      ) extends  Model

case class SizeModel(
                    height: Int = 0,
                    weight: Int = 0
                    ) extends  Model

case class TimeModel(
                    start: String = "",
                    end: String = ""
                    ) extends  Model

case class ImgModel(
                   id: Int = 0,
                   enabled: Seq[LangModel] = Seq.empty[LangModel],
                   time: String = "",
                   title: Map[LangModel, String] = Map.empty[LangModel, String],
                   url: String = "",
                   active: Boolean = true,
                   `type`: String = "",
                   areas: Seq[AreaModel] = Seq.empty[AreaModel]
                   ) extends  Model

case class AreaModel(
                    title: Map[LangModel, String] = Map.empty[LangModel, String],
                    shape: String = "",
                    hotspots: Map[LangModel, String] = Map.empty[LangModel, String],
                    href: String = ""
                    ) extends  Model

case class EffectModel(
                      `type`: String = "",
                      event: String = "",
                      auto: Boolean = true,
                      time: String = ""
                      ) extends  Model

object BannerModel {
  val default = BannerModel()
}
