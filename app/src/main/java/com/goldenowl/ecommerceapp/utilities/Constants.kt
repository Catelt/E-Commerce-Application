package com.goldenowl.ecommerceapp.utilities

const val IS_FIRST = "IS_FIRST"
const val REQUEST_SIGN_IN = 101
const val REQUEST_PICK_IMAGE = 102
const val REQUEST_CAMERA = 103
const val USER_FIREBASE = "users"
const val PRODUCT_FIREBASE = "products"
const val FAVORITE_FIREBASE = "favorites"
const val BAG_FIREBASE = "bags"
const val REVIEW_FIREBASE = "reviews"
const val PROMOTION_FIREBASE = "promotions"
const val ADDRESS_USER = "address"
const val PAYMENT_USER = "payment"
const val ORDER_USER = "order"
const val BLOCK_DEVICE = "block"
const val BASE_LINK_PRODUCT = "https://zetashop.page.link/products/"

const val ID_USER = "idUser"
const val ID_PRODUCT = "idProduct"
const val ID_ADDRESS = "idAddress"
const val ID_PROMOTION = "idPromotion"
const val URL_IMAGE = "urlImage"
const val ID_ORDER = "idOrder"

const val CATEGORY_NAME = "categoryName"
const val CREATED_DATE = "createdDate"
const val TIME_CREATE = "timeCreated"
const val SALE_PERCENT = "salePercent"
const val STATUS_ORDER = "status"
const val NUMBER_REVIEWS = "numberReviews"
const val REVIEW_STARS = "reviewStars"
const val NAME_CATEGORY = "nameCategories"
const val NAME_PRODUCT = "nameProduct"
const val HELPFUL = "helpful"

const val REQUEST_KEY = "request_key"
const val BUNDLE_KEY_NAME = "bundle_name"
const val BUNDLE_KEY_POSITION = "bundle_position"
const val BUNDLE_KEY_MIN = "bundle_min"
const val BUNDLE_KEY_MAX = "bundle_max"
const val BUNDLE_DISMISS = "dismiss"
const val BUNDLE_KEY_IS_FAVORITE = "bundle_is_favorite"
const val BUNDLE_KEY_NAME_COUNTRY = "bundle_name_country"

const val SIZE = "size"
const val COLOR = "color"
const val HISTORY = "HISTORY"
const val SALE = "Sale"
const val NEW = "New"
const val NULL = "NULL"
const val LIMIT = 4
const val GRIDVIEW_SPAN_COUNT_PRODUCT = 2
val statuses = listOf("Delivered", "Processing", "Cancelled")
const val DELIVERED = 0
const val PROCESSING = 1
const val CANCELLED = 2

const val WARNING_CHECK_AGAIN = "Please check again"
const val WARNING_CHECK_INTERNET = "Please check internet"
const val WARNING_SOMETHING_WRONG = "Something wrong"

val NON_DIGITS = Regex("[^\\d]")
const val MIN = 100000
const val MAX = 999999
const val TRACKING_NUMBER = "IW3475453455"

const val PRIVATE_KEY =
    "MIICXQIBAAKBgQCS3jRCamZ6RVE2QdFTAIlhdN55FMcqCxX+UlPrbMT/hYQrS1XF\n" +
            "jHcbtTAmAJ6sjLHkoXq7D+BCE0mzOwI7ZS9dtGHneeLJnLle9K/sp1vk17nCFyqS\n" +
            "SabHmoZewpeT7jPGo8TM+wCNYE1U4+yKAOWCaHpSQZoVAWHeVf6rM9FvgwIDAQAB\n" +
            "AoGAB2rtnMUe8+H4XhMo7VZDmgZ7DgZ8oKT6sWObFXLi6DzoFf+yD04u3dli2yiA\n" +
            "2xooCfIV7si4fN3YhRLf4/wSTkegC6o965k2C3elnjRnT6/FANi2SfdaDUctQGkF\n" +
            "dFuUmYUK+UYS+6XdvkIUDm04Id1K9CkefgUnO5PtKhrhqfECQQDQz9bi3Sb17/te\n" +
            "vDVEQ0QLW1tljZ6iRnkMuJuMmhRnwEsiC3Ylt/jLvUFZF9r6jmDZh8lw5n1vtZw4\n" +
            "WGdF/x7fAkEAtA7LRRJ8gxUhj18T4r826WVQcQXGNHIL8f9LAFNlJioT5Zxjlzxh\n" +
            "x1iJHXIcmKCFhyZlO4luJDcbWwBJzapX3QJBAKnRzi5Us2HimSlzoVJx7j3aP5t4\n" +
            "sKHC9KF+5apUiPHT9oaqkZydYS65LRM7DnElJ+jhwNcb7LnUFkfQxbSshjsCQDc0\n" +
            "NfQWZ+BUkTaAwvsI/w/txQzx5UPdF3Z5fAg8iU17JyCFoy8SDzRsG5XQj53n0SiP\n" +
            "iAgJ6Z/m2Vc8LmSE7m0CQQCIt1gnu9GmbBd68w/wLk89BhOcZlCHQm09A+2axXUx\n" +
            "Lc5VoNQrgLqNL0a6EhoqhZDPzKo/qJ/E2M+P2YKqnkEK\n"