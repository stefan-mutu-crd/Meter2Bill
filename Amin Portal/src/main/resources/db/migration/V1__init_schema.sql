-- Initial password for every user: ChangeMe123!

INSERT INTO admin_user
(username,
 password,
 enabled,
 full_name,
 role,
 creation_date,
 last_modified_date,
 version)
VALUES ('principal01', '$2a$10$pYb.tG5V4gxBBshRJSCJe.z8W1GjhKf1LreAOaHZtCvfRBf03QabO', TRUE,
        'Principal Admin 01', 'PRINCIPAL', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP,0),

       ('maintainer01', '$2a$10$dLTVvl0setmK1n9SOm5iK.PFfkGak.V1pFFLu9KzD1Tysl7XO/Xaq', TRUE,
        'Maintainer User 01', 'MAINTAINER', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP,0),

       ('audit01', '$2a$10$PA659FlxfWgjnt4IHVqr1uwXOhvA3YjYFEviOzHWOLWYZGTET76Vq', TRUE,
        'Audit User 01', 'AUDIT', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP,0),

       ('economist01', '$2a$10$4S621hsUDVLhbv7Ol60rVuw3Bu75arMkx2P62SuNSsozbrF8StLhO', TRUE,
        'Economist User 01', 'ECONOMIST', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP,0),

       ('principal02', '$2a$10$bq20AT4yhUOzhjgYgjcL7OWW6.fytXYurXz1pm/plzVFozoWXiEY2', TRUE,
        'Principal Admin 02', 'PRINCIPAL', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP,0),

       ('maintainer02', '$2a$10$mxqMewpKnzhGokPShftmSOcKJMn3Y70jYFER3ViLUmBLAlpbalyT6', TRUE,
        'Maintainer User 02', 'MAINTAINER', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP,0),

       ('audit02', '$2a$10$JYk7OijNMtz2nuq28oQ6letYzf4.HazI4DlUcowfX28lQ56LHGmJC', TRUE,
        'Audit User 02', 'AUDIT', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP,0),

       ('economist02', '$2a$10$ryuOoPDy5BP4rD5XNs75hufyaGIND5NywdrBzKka/pqpeOBwc2/0e', TRUE,
        'Economist User 02', 'ECONOMIST', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP,0),

       ('principal03', '$2a$10$7kl./J5SfJU5DI87jFt/VeBWESBiYeyvwswRUeZtQPE05h88e/Bjm', TRUE,
        'Principal Admin 03', 'PRINCIPAL', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP,0),

       ('maintainer03', '$2a$10$iE6mqyTioAxcOzkBUuUZBuDbxzhEu5zUaknk.pYEe6OjJfZ1SVHdq', TRUE,
        'Maintainer User 03', 'MAINTAINER', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP,0),

       ('audit03', '$2a$10$p.g0PwSgHmmyBeQZ4KvISe0WAwJFHukXNB7w.QK5ouE5gbSvznc4q', TRUE,
        'Audit User 03', 'AUDIT', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP,0),

       ('economist03', '$2a$10$7PYqEkNxlMCTQNYA2Gz5GeQrYojOiTuJd68i6zyQqmgHG1k3yBFse', TRUE,
        'Economist User 03', 'ECONOMIST', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP,0),

       ('principal04', '$2a$10$YlZA.CrLLFntlRsd/v7lbef9ZvgIUw6DlHjqsra2P74X7uJL1subW', TRUE,
        'Principal Admin 04', 'PRINCIPAL', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP,0),

       ('maintainer04', '$2a$10$iqhGyxBJvl8b2tE7DSSohuBNhFhqIfkuxyghqo97NkDfKV8nyqcDa', TRUE,
        'Maintainer User 04', 'MAINTAINER', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP,0),

       ('audit04', '$2a$10$iZ6f88jKGwimry6xaCTOT.0LhyikJK33Ts1alsIxdYsDEzXzpSzym', TRUE,
        'Audit User 04', 'AUDIT', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP,0),

       ('economist04', '$2a$10$h3NJpFFhExoBNEWabvNav.2G.ZkmGf.0xVgqpdcRA8LPQlI5OGwVq', TRUE,
        'Economist User 04', 'ECONOMIST', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP,0),

       ('principal05', '$2a$10$/jz80iRaLEQTGpcO3xDY..uLW1Oi4eXSWiZAkD9YSpK4GoqNYknsq', TRUE,
        'Principal Admin 05', 'PRINCIPAL', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP,0),

       ('maintainer05', '$2a$10$G/yZBUsP09cYyf9is1mX6OSGGgfwrNLoWujqHLBhOo3XNhg0qYl8W', TRUE,
        'Maintainer User 05', 'MAINTAINER', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP,0),

       ('audit05', '$2a$10$3jMSFsHmbjA1/pubThQf2OZ1C9PSuWq1JWuPps.Q8ADrj1Yaismxi', TRUE,
        'Audit User 05', 'AUDIT', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP,0),

       ('economist05', '$2a$10$b.W8m1f3mVPrWklPZsEML.2IScWBHrdWqgY.gNiC9fsZhhZzNrE7q', TRUE,
        'Economist User 05', 'ECONOMIST', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP,0),

       ('principal06', '$2a$10$nhryPmB88b0syH.BjWc4SupVYClPfx0vQS3sijLHZt/lGmdA5TPGC', TRUE,
        'Principal Admin 06', 'PRINCIPAL', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP,0),

       ('maintainer06', '$2a$10$.e4bTtBVZUsSZrkKscPKteK0Q4evzKXOXt0lQng0J2rjvHT.AqcSe', TRUE,
        'Maintainer User 06', 'MAINTAINER', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP,0),

       ('audit06', '$2a$10$kxdShlzmRbtwqKyhPH.jIuKZhpJ.xwIAHj/M3b6cRfQvQFu1tML9y', TRUE,
        'Audit User 06', 'AUDIT', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP,0),

       ('economist06', '$2a$10$tYDG12o5W0kUlWbq7EtKkeC0MGg/Lj6qrENUKoP7fFbfGPmbKRVMq', TRUE,
        'Economist User 06', 'ECONOMIST', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP,0),

       ('principal07', '$2a$10$WC.4wr0hY7obUkINDjxMA.9U8QZn2u2qB2Tx2iHdl.RZJalqRTbca', TRUE,
        'Principal Admin 07', 'PRINCIPAL', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP,0),

       ('maintainer07', '$2a$10$UNoJsC.x6myj.E8KlpBGgeD344HiaHV.Wp6JqPwf/xEdFUmceULDm', TRUE,
        'Maintainer User 07', 'MAINTAINER', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP,0),

       ('audit07', '$2a$10$JZw9NZ.5on.oFcd844.AOuiHdDROr6FRYsbSuFKhW6SgQmQLM2VMq', TRUE,
        'Audit User 07', 'AUDIT', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP,0),

       ('economist07', '$2a$10$4YdOZgiLyTGCwbe.Yrv.bOA9BbfbDp1pM4TY0GBxNanpw/Y.3rXWa', TRUE,
        'Economist User 07', 'ECONOMIST', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP,0),

       ('principal08', '$2a$10$M7PEuyPXxH3739lPWxP8KeSX8vfS1PVvLM55xVFdGIaOklaFuq3g.', TRUE,
        'Principal Admin 08', 'PRINCIPAL', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP,0),

       ('maintainer08', '$2a$10$X99oUJYc/650sHl6racQNembv575kApP8khpzYY5poVWNoT5g3DYC', TRUE,
        'Maintainer User 08', 'MAINTAINER', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP,0),

       ('audit08', '$2a$10$uQOLzj1bX72V4qaU31BKDuS2bdB./OxG6zfk4Xaz76WlLqZ5Eno9C', TRUE,
        'Audit User 08', 'AUDIT', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP,0),

       ('economist08', '$2a$10$HHLBh3yT1rRgE74uhG2K..V8R46Ix9p73WkHflqHHCoyDV24Umy.O', TRUE,
        'Economist User 08', 'ECONOMIST', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP,0),

       ('principal09', '$2a$10$PgRPiApoEEg4p0hH2B/L3OL0tXc9Fm3Cx5jfKdZWkmk2YIKgGfyZC', TRUE,
        'Principal Admin 09', 'PRINCIPAL', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP,0),

       ('maintainer09', '$2a$10$pdJHtXyJFXdWTSotZiL5Eun6SVz0xcPbuBzA3ppqlf/5bVHelLPw2', TRUE,
        'Maintainer User 09', 'MAINTAINER', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP,0),

       ('audit09', '$2a$10$u/p6HU1HE8XLnihfguJoB.c0jLNMBn8q5ctQmtfCIhQWMJ0KRGMYC', TRUE,
        'Audit User 09', 'AUDIT', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP,0),

       ('economist09', '$2a$10$jpATTSaQVOzr3PQTMJLhr.bvljQCAP8xKlQM47PNPmuQ3seEZkd/a', TRUE,
        'Economist User 09', 'ECONOMIST', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP,0),

       ('principal10', '$2a$10$OqnrFDCw2/tDBLUng./TvuZ8GnvZUuRhJxpsr0Me6hQ9/wiMNOaDm', TRUE,
        'Principal Admin 10', 'PRINCIPAL', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP,0),

       ('maintainer10', '$2a$10$f/Mczuaqs67rRaNKE3IdcugNkDJyZ5fYc0Kw/CU/FYO1UPJ23rKAW', TRUE,
        'Maintainer User 10', 'MAINTAINER', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP,0),

       ('audit10', '$2a$10$eCjJ75dEsdqdyTctwmKSt.mhVDQV3EL19y6s2qk7q21aidpK.9udW', TRUE,
        'Audit User 10', 'AUDIT', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP,0),

       ('economist10', '$2a$10$3WAM2Qw7OBxTInKVIWpNJexUdTWuBZxUm7T.gFgkvM1PHuHNM8Dcy', TRUE,
        'Economist User 10', 'ECONOMIST', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP,0),

       ('principal11', '$2a$10$.sh/v6tyX7dQRy.kHVXD5e64wxpuLvbZXdsbWOHqOqPNN5Sjg7el6', TRUE,
        'Principal Admin 11', 'PRINCIPAL', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP,0),

       ('maintainer11', '$2a$10$tNqj8ITIB3syO0lbw.3PMe8egkoYjSjbCbIT/O7LbIKXHPyw.y7mq', TRUE,
        'Maintainer User 11', 'MAINTAINER', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP,0),

       ('audit11', '$2a$10$SYSh77nKp6k43qUnLKxhPOUXyH4Ah38SYMx/TkYcRT6U159IJILZq', TRUE,
        'Audit User 11', 'AUDIT', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP,0),

       ('economist11', '$2a$10$anDtbkKOwsiymq/58stG3OMK6SLeqvWWXeAh0ZrZjysXjDYVJ2Hki', TRUE,
        'Economist User 11', 'ECONOMIST', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP,0),

       ('principal12', '$2a$10$OzkqfllRXMr0UxAkDBnacuu9Zux.QtIDp6jnWEZkmbm6ec33MbdmO', TRUE,
        'Principal Admin 12', 'PRINCIPAL', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP,0),

       ('maintainer12', '$2a$10$oDVPEqFQS2jh.zF1WlvTruVZ2VjOp3dSO8e1XcWV3ynTsdJcr6xyW', TRUE,
        'Maintainer User 12', 'MAINTAINER', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP,0),

       ('audit12', '$2a$10$P.LANa/DFzpV8WghilFuuOvy.aJMt2YofQV2EoIfIJSz4qLAaLAFm', TRUE,
        'Audit User 12', 'AUDIT', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP,0),

       ('economist12', '$2a$10$D2Xqg/Q1F/x3vslPr/zLjOibHO3DwAmSleSnIj/.C6fKL/XnJPLBe', TRUE,
        'Economist User 12', 'ECONOMIST', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP,0),

       ('principal13', '$2a$10$ejd6wlO.FUaGmKmG6p8CgOgqSIIOCm0/f/krVxBLS2PuABvt6F8TG', TRUE,
        'Principal Admin 13', 'PRINCIPAL', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP,0),

       ('maintainer13', '$2a$10$VLJt/VqutFsfyE5VbQSYLOJEXF7zIb9spfn7vQ9JNfdwrvDcYNEji', TRUE,
        'Maintainer User 13', 'MAINTAINER', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP,0),

       ('audit13', '$2a$10$mnvkw/YTsXPX33v0cNIFuO81Nz4yNpigLHGjdOJJxGB0gESqdCxSe', TRUE,
        'Audit User 13', 'AUDIT', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP,0),

       ('economist13', '$2a$10$WjdUT/s1Umi6H/0NAm0VFevGi.xUIi6VkoDY4sCCCqHwBGYRBCMqW', TRUE,
        'Economist User 13', 'ECONOMIST', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP,0),

       ('principal14', '$2a$10$NwcDJtQXyC842WOpZ3Xe4.vCEBv0GU7pMXqAYeacHCCABX7RaoMTu', TRUE,
        'Principal Admin 14', 'PRINCIPAL', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP,0),

       ('maintainer14', '$2a$10$ZIvdIl4wo/PRVdZwl0H0WuErljng0kzta76wqqqUUoakzhLUoOT.m', TRUE,
        'Maintainer User 14', 'MAINTAINER', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP,0),

       ('audit14', '$2a$10$RZMScK594gMVNP1/EXjcZuVtmHP1BvwRWc1GHRE0AhZ8Jt4oJaQPa', TRUE,
        'Audit User 14', 'AUDIT', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP,0),

       ('economist14', '$2a$10$4OaRP33mIormH63FaG6LuulfSfPJLUudFI75oQXhmFTtUVYilB3fW', TRUE,
        'Economist User 14', 'ECONOMIST', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP,0),

       ('principal15', '$2a$10$MNNYK6ueNT2LujNp/DaAPui5Tdub27UuERxofbYNMbHqJb7mJRCTa', TRUE,
        'Principal Admin 15', 'PRINCIPAL', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP,0),

       ('maintainer15', '$2a$10$nZxhLSOXIUv28XF2Q0Jaoe4bOmlubYzlNHFUPgL2G.9MaXG/FK0S.', TRUE,
        'Maintainer User 15', 'MAINTAINER', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP,0),

       ('audit15', '$2a$10$kWIpZv5.d1hj.cEy.4MZl.DXGMt8qTwDde/phIHOB/6AFHZZ0MvfC', TRUE,
        'Audit User 15', 'AUDIT', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP,0),

       ('economist15', '$2a$10$EvlicwJiiYAOUnXcNRjNjuoC0lAk7DZp/WWP6BH6UWbzD/.A0oowe', TRUE,
        'Economist User 15', 'ECONOMIST', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP,0);